package com.todo.noteservice.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;

/**
 * purpose: Note Bean to describe properties
 * @author JAYANTA ROY
 * @version 1.0
 * @since 18/07/18
 */
public class Note implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@ApiModelProperty(hidden =true)
	private String _id;
    @ApiModelProperty(hidden=true)
	private String authorId;
	private String title;
	private String description;
	
	@ApiModelProperty(hidden=true)	
	private String dateOfCreation;
	
	@ApiModelProperty(hidden=true)

	private String lastDateOfModified;

	public Note() {
	}

	public String get_id() {
		return _id;
	}
	@Id
	public void set_id(String _id) {
		this._id = _id;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public String getLastDateOfModified() {
		return lastDateOfModified;
	}

	public void setLastDateOfModified(String lastDateOfModified) {
		this.lastDateOfModified = lastDateOfModified;
	}

	@Override
	public String toString() {
		return "Note [_id=" + _id + ", authorId=" + authorId + ", title=" + title + ", description=" + description
				+ ", dateOfCreation=" + dateOfCreation + ", lastDateOfModified=" + lastDateOfModified + "]";
	}
}
