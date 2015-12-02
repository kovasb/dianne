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
package be.iminds.iot.dianne.rl.pong;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import be.iminds.iot.dianne.api.rl.Environment;
import be.iminds.iot.dianne.api.rl.EnvironmentListener;
import be.iminds.iot.dianne.rl.pong.api.PongEnvironment;
import be.iminds.iot.dianne.tensor.Tensor;
import be.iminds.iot.dianne.tensor.TensorFactory;

/**
 * Simple Pong environment in which an agents plays against an AI trying to
 * match the vertical position of the ball.
 * 
 * @author smbohez
 *
 */
@Component(immediate = true,
	property = { "name="+Pong.NAME, "aiolos.unique=be.iminds.iot.dianne.api.rl.Environment" })
public class Pong implements PongEnvironment, Environment {
	
	public static final String NAME = "Pong";
	
	private TensorFactory factory;

	private Set<EnvironmentListener> listeners = Collections.synchronizedSet(new HashSet<>());
	
	// paddle length and width
	private float pl = 0.3f;  
	private float pw = 0.05f; 
	// bounds
	private float b = 0.8f;
	// ball radius
	private float rad = 0.025f;
	// speed
	private float vdef = 0.012f; 
	// speedup when bouncing
	private float m = 1.5f;
	// frame skip for sparser sample generation
	private int skip = 5;
	// check if in terminal state
	private boolean terminal = false;
	
	private Tensor observation = null;
	
	// state
	private float x, y, vx, vy, p, o;
	
	private boolean resetPaddles = true;
	private boolean randomStart = false;
	private boolean terminalState = false;
	
	// AI
	private boolean ai = true;
	private int agentAction = 0;
	private int opponentAction = 0;
	
	@Activate
	void activate(BundleContext context) {
		String l = context.getProperty("be.iminds.iot.dianne.rl.pong.paddleLength");
		if (l != null)
			this.pl = Float.parseFloat(l);

		String vdef = context.getProperty("be.iminds.iot.dianne.rl.pong.defaultSpeed");
		if (vdef != null)
			this.vdef = Float.parseFloat(vdef);

		String rp = context.getProperty("be.iminds.iot.dianne.rl.pong.resetPaddles");
		if (rp != null)
			this.resetPaddles = Boolean.parseBoolean(rp);
		
		String rs = context.getProperty("be.iminds.iot.dianne.rl.pong.randomStart");
		if (rs != null)
			this.randomStart = Boolean.parseBoolean(rs);
		
		String sk = context.getProperty("be.iminds.iot.dianne.rl.pong.skip");
		if (sk != null)
			this.skip = Integer.parseInt(sk);
		
		String ts = context.getProperty("be.iminds.iot.dianne.rl.pong.terminalState");
		if (ts != null){
			this.terminalState = Boolean.parseBoolean(ts);
			if(terminalState){
				// also reset paddles in case of terminal state
				resetPaddles = true;
			}
		}
		
		reset();
		
		observation = factory.createTensor(new float[] { x, y, vx, vy, p, o }, 6);
	}

	@Deactivate
	void deactivate() {

	}

	@Override
	public float performAction(Tensor action) {
		agentAction = ((action.get(0) > 0) ? 1 : (action.get(1) > 0) ? 0 : -1);
		if(ai){
			updateAI();
		}
		
		float totalReward = 0;

		for(int i=0;i<skip;i++){
			// immediate return if terminal state
			if(terminal){
				return totalReward;
			}

			float reward = 0;
			
			float d_p = vdef * agentAction;
			float d_o = vdef * opponentAction;
	
			p += d_p;
			o += d_o;
	
			p = Math.min(1*b - pl / 2, p);
			p = Math.max(pl / 2 - 1*b, p);
	
			o = Math.min(1*b - pl / 2, o);
			o = Math.max(pl / 2 - 1*b, o);
	
			x += vx;
			y += vy;
	
			if (y - rad < -1 * b) {
				y = -b + rad;
				vy = -vy;
			} else if (y + rad > 1 * b) {
				y = b - rad;
				vy = -vy;
			}
	
	
			if (x - rad - pw < -1) {
				if(onPaddle(p)){
					vx = -vx;
					vy += agentAction*vdef/2;
					
					x = -1 + rad + pw;
				} else if (x < -1) {
					reward = -1;
					
					if(terminalState){
						// end
						terminal = true;
					} else {
						reset();
					}
				}
			} else if (x + rad + pw > 1) {
				if(onPaddle(o)){
					vx = -vx;
					vy += opponentAction*vdef/2;
					
					x = 1 - rad - pw;
				} else if (x > 1){
					reward = 1;
					
					if(terminalState){
						// end
						terminal = true;
					} else {
						reset();
					}
				}
			}
			
			totalReward += reward;
	
			final float r = reward;
			observation = factory.createTensor(new float[] { x, y, vx, vy, p, o }, 6);

			synchronized(listeners){
				listeners.stream().forEach(l -> l.onAction(r, observation));
			}
			
		}
		
		return totalReward;
	}
	
	private boolean onPaddle(float paddle){
		return paddle - pl / 2 - rad < y &&  y < paddle + pl / 2 + rad;
	}
	
	
	private void updateAI() {
		if (y < o - pl/2 )
			opponentAction = -1;
		else if (y > o + pl/2)
			opponentAction = 1;
		else
			opponentAction = 0;
	}

	@Override
	public Tensor getObservation() {
		if(terminal){
			return null;
		}
		return observation;
	}

	@Override
	public void reset() {
		// reset ball position
		x = y = 0;
		// reset paddles ?
		if(resetPaddles){
			p = o = 0;
		}

		double r = Math.random();
		r = (r < 0.5) ? 3 * Math.PI / 4 + r * Math.PI : -Math.PI / 4 + (r - 0.5) * Math.PI;
		
		// fixed start position ?
		if(!randomStart){
			r = - Math.PI;
		}
		
		vx = vdef * (float) Math.cos(r);
		vy = vdef * (float) Math.sin(r);
		
		terminal = false;
		
		observation = factory.createTensor(new float[] { x, y, vx, vy, p, o }, 6);
		listeners.stream().forEach(l -> l.onAction(0, observation));
	}

	@Reference
	void setTensorFactory(TensorFactory factory) {
		this.factory = factory;
	}
	
	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	void addEnvironmentListener(EnvironmentListener l, Map<String, Object> properties){
		String target = (String) properties.get("target");
		if(target==null || target.equals(NAME)){
			listeners.add(l);
		}
	}
	
	void removeEnvironmentListener(EnvironmentListener l){
		listeners.remove(l);
	}

	@Override
	public float getBounds() {
		return b;
	}

	@Override
	public float getSpeed() {
		return vdef;
	}

	@Override
	public float getPaddleLength() {
		return pl;
	}

	@Override
	public float getPaddleWidth() {
		return pw;
	}

	@Override
	public float getBallRadius() {
		return rad;
	}

	@Override
	public void useAI(boolean ai) {
		this.ai = ai;
	}

	@Override
	public void setOpponentAction(int action) {
		if(!ai){
			this.opponentAction = action;
		}
	}
	
}
