/**
 * 
 */
package edu.kit.aifb.mirrormaze.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Serialized;

/**
 * @author mugglmenzel
 * 
 */

@Entity
@Cached
@Indexed
public class Software implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Parent
	private Key<Ami> ami;

	@Indexed
	private String name;

	@Indexed
	private String version;

	@Serialized
	private Map<String, String> attributes = new HashMap<String, String>();

	/**
	 * 
	 */
	public Software() {
		super();
	}

	/**
	 * @param id
	 */
	public Software(Long id) {
		super();
		this.id = id;
	}

	/**
	 * @param ami
	 * @param name
	 * @param version
	 */
	public Software(Key<Ami> ami, String name, String version) {
		super();
		this.ami = ami;
		this.name = name;
		this.version = version;
	}

	/**
	 * @param ami
	 * @param name
	 * @param version
	 * @param attributes
	 */
	public Software(Key<Ami> ami, String name, String version,
			Map<String, String> attributes) {
		super();
		this.ami = ami;
		this.name = name;
		this.version = version;
		this.attributes = attributes;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the ami
	 */
	public Key<Ami> getAmi() {
		return ami;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param ami
	 *            the ami to set
	 */
	public void setAmi(Key<Ami> ami) {
		this.ami = ami;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

}
