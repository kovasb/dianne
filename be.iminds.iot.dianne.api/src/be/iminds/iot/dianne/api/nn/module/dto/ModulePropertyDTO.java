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
package be.iminds.iot.dianne.api.nn.module.dto;

/**
 * A configuration property for a Module.
 * 
 * Consists of a human-readable name, and id used as property key,
 * and the name of the expected class type.
 * 
 * @author tverbele
 *
 */
public class ModulePropertyDTO {

	// human-readable name for this property
	public final String name;
	
	// id to be used as property key in an actual configuration
	public final String id;
	
	// clazz that is expected as property value
	public final String clazz;
	
	public ModulePropertyDTO(String name, String id, String clazz){
		this.name = name;
		this.id = id;
		this.clazz = clazz;
	}
}
