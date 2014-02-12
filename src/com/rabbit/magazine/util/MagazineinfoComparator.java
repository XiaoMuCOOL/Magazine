package com.rabbit.magazine.util;

import java.util.Comparator;

import com.rabbit.magazine.Magazineinfo;

public class MagazineinfoComparator implements Comparator<Magazineinfo>{

	@Override
	public int compare(Magazineinfo m1, Magazineinfo m2) {
		/*if(m1.getUpdatetick()==null && m2.getUpdatetick()==null ){
			return 0;
		}else if(m1.getUpdatetick()==null && m2.getUpdatetick()!=null ){
			return -1;
		}else if(m1.getUpdatetick()!=null && m2.getUpdatetick()==null ){
			return 1;
		}*/
		
		if(m1.getUpdatetick().compareTo(m2.getUpdatetick())<0){			
			return 1;
		}else{
			return -1;
		}
	}

}
