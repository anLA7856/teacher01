package csust.teacher.info;

import java.io.Serializable;
/**
 * 与数据库的course表对应
 * @author U-anLA
 *
 */
public class Course implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int course_id;
	private String course_name;
	private String teacher_username;

	public Course() {
		super();

	}

	public int getCourse_id() {
		return course_id;
	}

	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}

	public String getCourse_name() {
		return course_name;
	}

	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}

	public String getTeacher_username() {
		return teacher_username;
	}

	public void setTeacher_username(String teacher_username) {
		this.teacher_username = teacher_username;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Course(int course_id, String course_name, String teacher_username) {
		super();
		this.course_id = course_id;
		this.course_name = course_name;
		this.teacher_username = teacher_username;
	}

	@Override
	public String toString() {
		return "Course [course_id=" + course_id + ", course_name="
				+ course_name + ", teacher_username=" + teacher_username + "]";
	}

}
