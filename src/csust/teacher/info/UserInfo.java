package csust.teacher.info;

import java.io.Serializable;

public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int teacher_id;
	private String teacher_name;
	private String teacher_username;
	private String teacher_password;
	private String teacher_wifimac;

	public int getTeacher_id() {
		return teacher_id;
	}

	public void setTeacher_id(int teacher_id) {
		this.teacher_id = teacher_id;
	}

	public String getTeacher_name() {
		return teacher_name;
	}

	public void setTeacher_name(String teacher_name) {
		this.teacher_name = teacher_name;
	}

	public String getTeacher_username() {
		return teacher_username;
	}

	public void setTeacher_username(String teacher_username) {
		this.teacher_username = teacher_username;
	}

	public String getTeacher_password() {
		return teacher_password;
	}

	public void setTeacher_password(String teacher_password) {
		this.teacher_password = teacher_password;
	}

	public String getTeacher_wifimac() {
		return teacher_wifimac;
	}

	public void setTeacher_wifimac(String teacher_wifimac) {
		this.teacher_wifimac = teacher_wifimac;
	}

	@Override
	public String toString() {
		return "UserInfo [teacher_id=" + teacher_id + ", teacher_name="
				+ teacher_name + ", teacher_username=" + teacher_username
				+ ", teacher_password=" + teacher_password
				+ ", teacher_wifimac=" + teacher_wifimac + "]";
	}

	public UserInfo(int teacher_id, String teacher_name,
			String teacher_username, String teacher_password,
			String teacher_wifimac) {
		super();
		this.teacher_id = teacher_id;
		this.teacher_name = teacher_name;
		this.teacher_username = teacher_username;
		this.teacher_password = teacher_password;
		this.teacher_wifimac = teacher_wifimac;
	}

	public UserInfo() {

	}

}
