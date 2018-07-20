package com.todo.noteservice.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.annotations.ApiModelProperty;

@Document(collection="label")
public class Label implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@ApiModelProperty(hidden = true)
	private String _id;
	
	 private String labelName;
	 //@DBRef
	// @Field("note")
	 @ApiModelProperty(hidden = true)
	 private String noteId;
	 
	 public Label() {}
	 
	
	public String get_id() {
		return _id;
	}

	@Id
	public void set_id(String _id) {
		this._id = _id;
	}


	public String getLabelName() {
		return labelName;
	}
	
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public String getNoteId() {
		return noteId;
	}
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}
	@Override
	public String toString() {
		return "Label [labelId=" + _id + ", labelName=" + labelName + ", noteId=" + noteId + "]";
	}
	 	
}
