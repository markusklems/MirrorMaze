package edu.kit.aifb.mirrormaze.client.datasources;

import java.util.List;

import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.kit.aifb.mirrormaze.client.model.Ami;

public class AmisDataSource {
	
	private List<Ami> amis;
	
	public ListGridRecord[] createListGridRecords() {
		
		
			
		ListGridRecord[] result = new ListGridRecord[amis.size()];
		
		int i = 0;
		
		for (Ami ami : amis) {
			result[i] = new ListGridRecord();
			result[i].setAttribute("name", ami.getName());
			result[i].setAttribute("id", ami.getImageId());
			//result[i].setAttribute("value", alt.getIndexResult());
			i++;
		}
		
		return result;
	}
	
	public void setAmis(List<Ami> amis) {
		this.amis = amis;
	}
	
}