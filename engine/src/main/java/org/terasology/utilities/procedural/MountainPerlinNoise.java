/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.utilities.procedural;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.TeraMath;
import org.terasology.utilities.random.FastRandom;
import org.terasology.math.geom.Vector2f;

/**
 * Improved Perlin noise based on the reference implementation by Ken Perlin.
 *
 * @deprecated Prefer using {@link SimplexNoise}, it is comparable to Perlin noise (fewer directional artifacts, lower computational overhead for higher dimensions).
 */
//@Deprecated
public class MountainPerlinNoise implements Noise2D {

    //prolly will have to play with this as current noise can return more than 1
    private Vector2f[][] grid;
    private static final Logger logger = LoggerFactory.getLogger(MountainPerlinNoise.class);

    /**
     * Init. a new generator with a given seed value.
     *
     * @param seed The seed value
     */
    public MountainPerlinNoise(long seed) {
        FastRandom rand = new FastRandom(seed);
        grid = new Vector2f[4][4];
        float angle = (float) Math.toRadians(rand.nextFloat(10.0f, 80.0f));
        grid[1][1] = new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
        angle = (float) Math.toRadians(rand.nextFloat(100.0f, 170.0f));
        grid[2][1] = new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
        angle = (float) Math.toRadians(rand.nextFloat(190.0f, 260.0f));
        grid[1][2] = new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
        angle = (float) Math.toRadians(rand.nextFloat(280.0f, 350.0f));
        grid[2][2] = new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (i == 0 | i == 3 | j == 0 | j == 3) {
                    grid[i][j] = new Vector2f(0f, 0f);
                    if (i == 0)
                        grid[i][j].setX(-1);
                    else if (i == 3)
                        grid[i][j].setX(1);
                    if (j == 0)
                        grid[i][j].setY(-1);
                    else if (j == 3)
                        grid[i][j].setY(1);
                    // not normalizing as i feel corner diagonals should be longer vectors to ensure polar uniformity
                }

    }

    /**
     * Returns the noise value at the given position.
     *
     * @param posX Position on the x-axis
     * @param posY Position on the y-axis
     * @return The noise value
     */
    @Override
    public float noise(float posX, float posY) {
        posX = posX - TeraMath.fastFloor(posX);
        posY = posY - TeraMath.fastFloor(posY);

        float[][] dots = new float[2][2];

        float scaledX = posX * 3;
        float scaledY = posY * 3;

        Vector2f point = new Vector2f(scaledX, scaledY);

        int lBoundX = (int) scaledX;
        int lBoundY = (int) scaledY;

        for (int i = 0; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
                Vector2f temp = new Vector2f(point);
                int gridI = i + lBoundX;
                int gridJ = j + lBoundY;
                dots[i][j] = grid[gridI][gridJ].dot(temp.sub(new Vector2f(gridI, gridJ)));
            }
        }

        float top = TeraMath.lerp(dots[0][0], dots[1][0], scaledX - lBoundX);
        float bottom = TeraMath.lerp(dots[0][1], dots[1][1], scaledX - lBoundX);

        float ret = TeraMath.lerp(top, bottom, scaledY - lBoundY);
        return Math.max(ret, 0f);
    }
}
