package csust.teacher.info;

import java.io.Serializable;

/**
 * 
 * @author anLA7856
 *
 */
public class SignInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sign_date;
	private String sign_courseName;
	private String sign_courseNum;
	private String sign_teacherName;
	private String allow_sign_id;

	public String getAllow_sign_id() {
		return allow_sign_id;
	}

	public void setAllow_sign_id(String allow_sign_id) {
		this.allow_sign_id = allow_sign_id;
	}

	public String getSign_date() {
		return sign_date;
	}

	public void setSign_date(String sign_date) {
		this.sign_date = sign_date;
	}

	public String getSign_courseName() {
		return sign_courseName;
	}

	public void setSign_courseName(String sign_courseName) {
		this.sign_courseName = sign_courseName;
	}

	public String getSign_courseNum() {
		return sign_courseNum;
	}

	public void setSign_courseNum(String sign_courseNum) {
		this.sign_courseNum = sign_courseNum;
	}

	public String getSign_teacherName() {
		return sign_teacherName;
	}

	public void setSign_teacherName(String sign_teacherName) {
		this.sign_teacherName = sign_teacherName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public SignInfo(String sign_date, String sign_courseName,
			String sign_courseNum, String sign_teacherName) {
		super();
		this.sign_date = sign_date;
		this.sign_courseName = sign_courseName;
		this.sign_courseNum = sign_courseNum;
		this.sign_teacherName = sign_teacherName;
	}

	public SignInfo() {

	}

	@Override
	public String toString() {
		return "SignInfo [sign_date=" + sign_date + ", sign_courseName="
				+ sign_courseName + ", sign_courseNum=" + sign_courseNum
				+ ", sign_teacherName=" + sign_teacherName + ", allow_sign_id="
				+ allow_sign_id + "]";
	}

}
