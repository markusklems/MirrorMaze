/**
 * 
 */
package de.eorganization.crawler.client.model;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;

/**
 * @author mugglmenzel
 * 
 */
@Entity
@Cached
@Indexed
public class SoftwareName {

	@Id
	private String name;

	public SoftwareName() {
		super();
	}

	public SoftwareName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
