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
package be.iminds.iot.dianne.dataset.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import be.iminds.iot.dianne.api.dataset.Dataset;
import be.iminds.iot.dianne.dataset.FileDataset;

/**
 * The Street View House Numbers dataset, based on the cropped 32x32 images
 * http://ufldl.stanford.edu/housenumbers/
 * 
 * @author tverbele
 *
 */
@Component(
		service={Dataset.class},
		immediate=true, 
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		configurationPid="be.iminds.iot.dianne.dataset.CSV",		
		property={"aiolos.unique=true"})
public class CSVDataset extends FileDataset{

	String separator = ",";
	
	@Override
	protected void init(Map<String, Object> properties){
		String file = (String)properties.get("file");
		if(file != null)
			inputFiles = new String[]{file};
		
		String s = (String)properties.get("separator");
		if(s != null){
			separator = s;
		}
	}
	
	@Override
	protected void parse(InputStream in, InputStream out) throws Exception{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String s;
		while ((s = reader.readLine()) != null) {
			String[] data = s.split(",");
			int i=0;
			for(i=0;i<inputSize;i++){
				inputs[count][i] = Float.parseFloat(data[i]);
			}
			if(data.length - i == 1 && outputSize > 1){
				// threat as class index?
				int index = Integer.parseInt(data[i]);
				outputs[count][index] = 1;
			} else {
				for(int k=0;k<outputSize;k++){
					outputs[count][k] = Float.parseFloat(data[i+k]);
				}
			}
			count++;
		}
	}
}
