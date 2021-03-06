/*******************************************************************************
 * DIANNE  - Framework for distributed artificial neural networks
 * Copyright (C) 2015  iMinds - IBCN - UGent
 *
 * This file is part of DIANNE.
 *
 * DIANNE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Tim Verbelen, Steven Bohez
 *******************************************************************************/
package be.iminds.iot.dianne.rl.experience;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import be.iminds.iot.dianne.api.dataset.Dataset;
import be.iminds.iot.dianne.api.rl.dataset.ExperiencePool;

@Component(
		service={ExperiencePool.class, Dataset.class},
		immediate=true, 
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		configurationPid="be.iminds.iot.dianne.dataset.ExperiencePool")
public class MemoryExperiencePool extends AbstractExperiencePool {

	private float[][] samples;

	@Override
	protected void setup() {
		try {
			samples = new float[maxSize][sampleSize];
		} catch(OutOfMemoryError e){
			System.err.println("Failed to setup Experience Pool "+name+" in memory: failed to allocate "+maxSize*sampleSize*4/1000000+" MB");
			throw new RuntimeException("Failed to instantiate experience pool, not enough memory");
		}
	}

	@Override
	protected float[] loadData(int index) {
		return samples[index];
	}

	@Override
	protected void writeData(int index, float[] data) {
		System.arraycopy(data, 0, samples[index], 0, data.length);
	}
}
