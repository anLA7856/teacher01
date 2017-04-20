package csust.teacher.info;

import java.io.Serializable;

/**
 * 用于展示Android中每门课的每一个人的签到记录
 * 
 * @author U-anLA
 *
 */
public class StudentSignRate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int rate;
	private String student_id;
	private String student_username;
	private String student_name;
	private String course_id;
	private int have_sign;
	private int allSignCount;

	public String getStudent_username() {
		return student_username;
	}

	public void setStudent_username(String student_username) {
		this.student_username = student_username;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public String getStudent_name() {
		return student_name;
	}

	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}

	public String getCourse_id() {
		return course_id;
	}

	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}

	public int getHave_sign() {
		return have_sign;
	}

	public void setHave_sign(int have_sign) {
		this.have_sign = have_sign;
	}

	public int getAllSignCount() {
		return allSignCount;
	}

	public void setAllSignCount(int allSignCount) {
		this.allSignCount = allSignCount;
	}

	@Override
	public String toString() {
		return "StudentSignRate [rate=" + rate + ", student_id=" + student_id
				+ ", student_username=" + student_username + ", student_name="
				+ student_name + ", course_id=" + course_id + ", have_sign="
				+ have_sign + ", allSignCount=" + allSignCount + "]";
	}

}
