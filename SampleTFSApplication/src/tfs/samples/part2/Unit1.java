package tfs.samples.part2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tfs.client.TFSClient;
import tfs.samples.part1.Test;

public class Unit1 extends Test {
	private int total;
	private int fanout;
	
	int count = 0;
	
	public Unit1(String masterIpAddress, int masterPort, int total, int fanout) {
		super(masterIpAddress, masterPort);
		
		this.total = total;
		this.fanout = fanout;
	}

	@Override
	public int execute() {
		if (total < 1)
			return TFSClient.CLIENT_ERROR;
		
		int ret = TFSClient.CLIENT_ERROR;
		try {
			ret = tfsClient.createDir("1", "/");
			printCreateDir(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (ret != TFSClient.OK) {
			return ret;
		}		
		
		count = 1;
		List<String> strs = new ArrayList<String>();
		strs.add("1");		
		
		ret = createSubDirs(strs);
		
		return ret;
	}
	
	private int createSubDirs(List<String> strs) {
		int ret = TFSClient.CLIENT_ERROR;
		
		for (String str : strs) {
			for (int i = 0; i < fanout; i++) {
				int dir = ++count;
				if (dir > total)
					return TFSClient.OK;
				
				try {
					ret = tfsClient.createDir(dir + "", str);
					printCreateDir(ret);
				} catch (IOException e) {
					ret = TFSClient.CONN_ERROR;
				}
				
				if (ret != TFSClient.OK) {
					return ret;
				}
			}
		}
		
		return ret;
	}
}
