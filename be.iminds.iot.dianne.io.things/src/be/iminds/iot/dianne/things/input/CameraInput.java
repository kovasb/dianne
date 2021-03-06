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
package be.iminds.iot.dianne.things.input;

import java.util.UUID;

import be.iminds.iot.dianne.api.nn.module.Input;
import be.iminds.iot.dianne.tensor.Tensor;
import be.iminds.iot.dianne.tensor.util.ImageConverter;
import be.iminds.iot.things.api.camera.Camera.Format;
import be.iminds.iot.things.api.camera.CameraListener;

public class CameraInput implements CameraListener {

	private final ImageConverter converter;
	
	private final Input input;
	
	private float[] buffer;
	
	// input frame
	private int width;
	private int height;
	private int channels;
	
	public CameraInput(Input input,
			int width, int height, int channels){
		this.converter = new ImageConverter();
		
		this.input = input;
		
		this.width = width;
		this.height = height;
		this.channels = channels;
		
		this.buffer = new float[channels*width*height];
	}
	
	
	@Override
	public void nextFrame(UUID id, Format format, byte[] data) {
		if(format==Format.MJPEG){
			try {
				Tensor in = converter.readFromBytes(data);
				input.input(in);
			} catch(Exception e){
				System.out.println("Failed to read jpeg frame");
			}
		} else if(format==Format.RGB){
			int k = 0;
			for(int c=0;c<channels;c++){
				for(int y=0;y<height;y++){
					for(int x=0;x<width;x++){
						float val = (data[c*width*height+y*width+x] & 0xFF)/255f;
						buffer[k++] = val;
					}
				}
			}
			Tensor in = new Tensor(buffer, channels, height, width);
			input.input(in);
		} 

	}


	
}
