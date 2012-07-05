package edu.kit.aifb.mirrormaze.client.datasources.responseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListResponse<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3074664186533412530L;

	int totalRecords;

	List<T> list;

	/**
	 * 
	 */
	public ListResponse() {
		super();
		totalRecords = 0;
		list = new ArrayList<T>();
	}

	/**
	 * @param totalRecords
	 * @param list
	 */
	public ListResponse(int totalRecords, List<T> list) {
		super();
		this.totalRecords = totalRecords;
		this.list = list;
	}

	/**
	 * @return the totalRecords
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @return the list
	 */
	public List<T> getList() {
		return list;
	}

	/**
	 * @param totalRecords
	 *            the totalRecords to set
	 */
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<T> list) {
		this.list = list;
	}

}
