package csust.teacher.info;

import java.io.Serializable;

/**
 * 
 * @author anLA7856
 *
 */
public class CourseInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 教师号
	 */
	private String teacherNum;
	/**
	 * 课程名
	 */
	private String teacherName;
	/**
	 * 课程号
	 */
	private String courseName;
	public String getTeacherNum() {
		return teacherNum;
	}
	public void setTeacherNum(String teacherNum) {
		this.teacherNum = teacherNum;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public CourseInfo(String teacherNum, String teacherName, String courseName) {
		super();
		this.teacherNum = teacherNum;
		this.teacherName = teacherName;
		this.courseName = courseName;
	}
	public CourseInfo() {

	}
	@Override
	public String toString() {
		return "CourseInfo [teacherNum=" + teacherNum + ", teacherName="
				+ teacherName + ", courseName=" + courseName + "]";
	}
	
	


}
