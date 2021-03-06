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
package be.iminds.iot.dianne.rl.agent.strategy;

import java.util.Map;

import be.iminds.iot.dianne.api.nn.NeuralNetwork;
import be.iminds.iot.dianne.api.rl.agent.ActionStrategy;
import be.iminds.iot.dianne.api.rl.agent.AgentProgress;
import be.iminds.iot.dianne.api.rl.environment.Environment;
import be.iminds.iot.dianne.tensor.Tensor;

// TODO ... we need to define a new generic mechanism to bind a "controller" to this 
// kind of strategy to manually control an agent, e.g. keyboard/gamepad/vr controllers
// mapping actions for a given environment via the setup?
public class ManualActionStrategy implements ActionStrategy {

	private Tensor action;
	
	public void setAction(Tensor a){
		this.action = a;
	}

	@Override
	public void setup(Map<String, String> config, Environment env, NeuralNetwork... nns) throws Exception {
	}

	@Override
	public AgentProgress processIteration(long i, Tensor state) throws Exception {
		return new AgentProgress(i, action);
	}
	
}
