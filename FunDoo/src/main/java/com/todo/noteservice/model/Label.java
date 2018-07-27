package com.todo.noteservice.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;

@Document(collection = "label")
public class Label implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@ApiModelProperty(hidden = true)
	@SequenceGenerator(name = "SEQ_GEN", sequenceName = "SEQ_USER", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
	private String _id;

	private String labelName;
	// @DBRef
	// @Field("note")
	@ApiModelProperty(hidden = true)
	private String noteId;
	@ApiModelProperty(hidden = true)
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Label() {
	}

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
