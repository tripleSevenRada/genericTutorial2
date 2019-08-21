package com.etnetera.hr.data;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Simple data entity describing basic properties of every JavaScript framework.
 * 
 * @author Etnetera
 *
 */
@Indexed
@Entity
public class JavaScriptFramework {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	//TODO
	private static final int JAVASCRIPT_FRAMEWORK_NAME_MAX_LENGTH = 30;
	@Column(nullable = false, length = JAVASCRIPT_FRAMEWORK_NAME_MAX_LENGTH)
	@NotBlank(message = "NotBlank")
	@Size(max = JAVASCRIPT_FRAMEWORK_NAME_MAX_LENGTH, message = "Size")
	private String name;

	public JavaScriptFramework() {
	}

	public JavaScriptFramework(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "JavaScriptFramework [id=" + id + ", name=" + name + "]";
	}

}
