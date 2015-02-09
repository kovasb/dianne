package be.iminds.iot.dianne.nn.module.join;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import be.iminds.iot.dianne.nn.module.AbstractModule;
import be.iminds.iot.dianne.nn.module.Module;
import be.iminds.iot.dianne.tensor.Tensor;
import be.iminds.iot.dianne.tensor.TensorFactory;

public abstract class Join extends AbstractModule {

	protected Map<UUID, Tensor> inputs = new HashMap<UUID, Tensor>();
	protected Map<UUID, Tensor> gradInputs = new HashMap<UUID, Tensor>();
	
	// this will make sure that one will wait until all prev have given input before forwarding
	protected boolean sync = true;
	protected Map<UUID, AtomicBoolean> prevLock;
	
	public Join(TensorFactory factory) {
		super(factory);
	}
	
	public Join(TensorFactory factory, UUID id) {
		super(factory, id);
	}
	
	protected void callPrevious(){
		// call all previous, BackwardJoinRunnable will make sure each gets part of the gradInputs
		for(Runnable r : prev){
			executor.execute(r);
		}
	}
	
	@Override
	public void forward(final UUID moduleId, final Tensor input) {
		this.inputs.put(moduleId, input);
		
		// when syncing, wait for input from each prev
		if(sync && prev!=null && prev.length>1){
			synchronized(prevLock){
				prevLock.get(moduleId).set(true);
				for(AtomicBoolean b : prevLock.values()){
					if(!b.get()){
						return;
					}
				}
				for(AtomicBoolean b : prevLock.values()){
					b.set(false);
				}
			}
		} 
		
		forward();
		
		if(next!=null)
			callNext();
	
		if(fwdListeners.size()>0)
			notifyForwardListeners();
	}
	
	@Override
	public void setPrevious(final Module... prev) {
		if(prev==null){
			this.prev = null;
		} else {
			this.prev = new BackwardJoinRunnable[prev.length];
			for(int i=0;i<prev.length;i++){
				this.prev[i] = new BackwardJoinRunnable(prev[i]);
			}
		}
	}
	
	private final class BackwardJoinRunnable implements Runnable {
		private final Module m;
		private final UUID prevId;
		
		public BackwardJoinRunnable(Module m){
			this.m = m;
			this.prevId = m.getId();
		}
		
		public void run(){
			m.backward(id, gradInputs.get(prevId));
		}
	}
}