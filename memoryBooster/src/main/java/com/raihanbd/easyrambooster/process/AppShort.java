package com.raihanbd.easyrambooster.process;

import java.util.Comparator;

public class AppShort implements Comparator {

	public int compare(TaskInfo info1, TaskInfo info2) {
		byte b = -1;
		if (info1 != null) {
			if (info2 == null) {
				b = 1;
			} else {
				long l = info1.mem;
				long ls = info2.mem;
				if (l <= ls)
					b = 1;
			}
		}
		return b;
	}

	@Override
	public int compare(Object obj1, Object obj2) {
		TaskInfo info1 = (TaskInfo) obj1;
		TaskInfo info2 = (TaskInfo) obj2;
		return compare(info1, info2);
	}

}
