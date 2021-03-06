/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

 /**
 * 
 */
package org.apache.ranger.biz;

import org.apache.log4j.Logger;
import org.apache.ranger.hadoop.client.HadoopFS;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * 
 */

@Component
@Scope("singleton")
public class HadoopFSMgr {

	private HadoopFS fs;
	private String dataSource;
	private static Logger logger = Logger.getLogger(HadoopFSMgr.class);

	public HadoopFSMgr() {
		init();
	}

	public HadoopFSMgr(String dataSource) {
		this.dataSource = dataSource;
		init();
	}

	private void init() {
		try {
//			if (dataSource != null) {
//				fs = new HadoopFS(dataSource);
//			} else {
//				fs = new HadoopFS("hadoopdev");
//			}
		} catch (Exception e) {
			logger.error("Error connecting hive client", e);
		}
	}

	protected HadoopFS getInstance(String dataSourceName) {
		if (dataSourceName == null) {
			logger.info("Hadoop client name not provided.");
			return fs;
		} else {
			if (fs.getDataSource() != null) {
				if (fs.getDataSource().equalsIgnoreCase(dataSourceName)) {
					return fs;
				} else {
					fs = new HadoopFS(dataSourceName);
					return fs;
				}
			} else {
				fs = new HadoopFS(dataSourceName);
				return fs;
			}
		}
	}

}
