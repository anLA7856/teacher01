package csust.teacher.info;

/**
 * 用于记录每一位学生某次签到记录
 * 
 * @author anLA7856
 *
 */
public class SignNameInfo {

	private String sign_time;
	private String studentUsername;
	private String studentName;
	private String student_id;
	private String sign_state;

	public String getSign_state() {
		return sign_state;
	}

	public void setSign_state(String sign_state) {
		this.sign_state = sign_state;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getSign_time() {
		return sign_time;
	}

	public void setSign_time(String sign_time) {
		this.sign_time = sign_time;
	}

	public String getStudentUsername() {
		return studentUsername;
	}

	public void setStudentUsername(String studentUsername) {
		this.studentUsername = studentUsername;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	@Override
	public String toString() {
		return "SignNameInfo [sign_time=" + sign_time + ", studentUsername="
				+ studentUsername + ", studentName=" + studentName
				+ ", student_id=" + student_id + ", sign_state=" + sign_state
				+ "]";
	}

}
