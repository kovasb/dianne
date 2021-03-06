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
package be.iminds.iot.dianne.api.coordinator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import be.iminds.iot.dianne.api.nn.eval.Evaluation;
import be.iminds.iot.dianne.api.nn.learn.LearnProgress;

/**
 * Summarizes the learn results
 * @author tverbele
 *
 */
public class LearnResult {

	public Map<UUID, List<LearnProgress>> progress = new HashMap<>();
	public Map<Long, Evaluation> validations = new HashMap<>();
	
	public LearnResult(){}
	
	public LearnResult(Map<UUID, List<LearnProgress>> p, Map<Long, Evaluation> v){
		this.progress = p;
		this.validations = v;
	}
	
	public long getIterations(){
		List<LearnProgress> p = progress.values().iterator().next();
		return p.get(p.size()-1).iteration;
	}
	
	public float getError(){
		List<LearnProgress> p = progress.values().iterator().next();
		return p.get(p.size()-1).miniBatchError;
	}
}
