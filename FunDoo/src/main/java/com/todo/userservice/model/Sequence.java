package com.todo.userservice.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * purpose: To maintain user sequence
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
@Document(collection = "sequence")
public class Sequence implements Serializable {

	private static final long serialVersionUID = 1L;

	private String _id;
	private int seq;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}
}
