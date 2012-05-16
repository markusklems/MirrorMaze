/**
 * 
 */
package edu.kit.aifb.mirrormaze.client.model;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Parent;

/**
 * @author mugglmenzel
 * 
 */

@Cached
@Indexed
public class Software {

	@Id
	private Long id;

	@Parent
	private Key<Ami> ami;

}
