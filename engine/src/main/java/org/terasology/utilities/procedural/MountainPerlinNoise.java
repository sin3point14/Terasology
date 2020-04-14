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

import org.terasology.math.TeraMath;
import org.terasology.utilities.random.FastRandom;
import org.terasology.math.geom.Vector2f;

/**
 * Improved Perlin noise based on the reference implementation by Ken Perlin.
 * @deprecated Prefer using {@link SimplexNoise}, it is comparable to Perlin noise (fewer directional artifacts, lower computational overhead for higher dimensions).
 *
 */
@Deprecated
public class MountainPerlinNoise extends AbstractNoise implements Noise2D {

    //prolly will have to play with this as current noise can return more than 1
    private Vector2f[][] grid;

    /**
     * Init. a new generator with a given seed value.
     *
     * @param seed The seed value
     */
    public MountainPerlinNoise(long seed) {
        FastRandom rand = new FastRandom(seed);
        Vector2f[][] grid = new Vector2f[2][2];
        for (int i=0; i<=1; i++) {
            for (int j = 0; j <= 1; j++) {
                float angle = rand.nextFloat(10.0f, 80.0f);
                grid[i][j].set((float) Math.cos(angle), (float) Math.sin(angle));
            }
        }
    }

    /**
     * Returns the noise value at the given position.
     *
     * @param posX Position on the x-axis
     * @param posY Position on the y-axis
     * @param posZ Position on the z-axis
     * @return The noise value
     */
    @Override
    public float noise(float posX, float posY) {

        Vector2f point = new Vector2f(posX, posY);
        float[][] gradients = new float[2][2];

        for (int i=0; i<=1; i++) {
            for (int j = 0; j <= 1; j++) {
                gradients[i][j] = grid[i][j].dot(point);
            }
        }

        float top = TeraMath.lerp(gradients[0][0], gradients[1][0], posX);
        float bottom = TeraMath.lerp(gradients[0][1], gradients[1][1], posX);

        return TeraMath.lerp(top, bottom, posY);
    }
}
