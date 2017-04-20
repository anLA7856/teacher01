package csust.teacher.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import csust.teacher.chartView.Score;
import csust.teacher.info.CourseInfo;
import csust.teacher.info.SignInfo;
import csust.teacher.info.SignNameInfo;
import csust.teacher.info.StudentInfo;
import csust.teacher.info.StudentSignRate;
import csust.teacher.info.UserInfo;

public class MyJson {
	/**
	 * 用于解析用户信息，也就是登录成功之后登录信息
	 * @param result
	 * @return
	 */
	public List<UserInfo> getUserInfoList(String result) {
		List<UserInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<UserInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				UserInfo info = new UserInfo();
				info.setTeacher_id(Integer.parseInt(job.getString("teacher_id")));
				info.setTeacher_name(job.getString("teacher_name"));
				info.setTeacher_password(job.getString("teacher_password"));
				info.setTeacher_username(job.getString("teacher_username"));
				info.setTeacher_wifimac(job.getString("teacher_wifimac"));
				list.add(info);
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		return list;
	}
	/**
	 * 用于把课程信息的json格式转化过来。
	 * @param result
	 * @return
	 */
	public List<CourseInfo> getCourseInfoList(String result) {
		List<CourseInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<CourseInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				CourseInfo info = new CourseInfo();

				info.setCourseName(job.getString("course_id"));
				info.setTeacherName(job.getString("course_name"));
				info.setTeacherNum(job.getString("teacher_username"));
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	/**
	 * 用于把正在签到的信息列出来
	 * @param result
	 * @return
	 */
	public List<SignInfo> getNotSignInfoList(String result) {
		List<SignInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<SignInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				SignInfo info = new SignInfo();
				info.setSign_courseName(job.getString("sign_courseName"));
				info.setSign_courseNum(job.getString("sign_courseNum"));
				info.setSign_date(job.getString("sign_date"));
				info.setSign_teacherName(job.getString("sign_teacherName"));
				info.setAllow_sign_id(job.getString("alow_sign_id"));
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获得实时签到记录
	 * @param result
	 * @return
	 */
	public List<SignNameInfo> getRealSignNameInfoList(String result){
		List<SignNameInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<SignNameInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				SignNameInfo info = new SignNameInfo();
				//暂时只用了这两个字段
				info.setSign_time(job.getString("sign_time"));
				info.setStudentName(job.getString("studentName"));
				info.setSign_state(job.getString("sign_state"));
				info.setStudent_id(job.getString("student_id"));
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	

	/**
	 * 解析某一次课程的签到率
	 * @param result
	 * @return
	 */
	public List<Score> getSignNameRateCountList(String result){
		List<Score> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<Score>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				Score info = new Score();
				//暂时只用了这两个字段
				
				info.setAllow_sign_id(job.getString("allow_sign_id"));
				info.setScore(job.getInt("score"));
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 解析某一次课程的签到率，主要是解析某一门课程的所有不同学生的签到率
	 * @param result
	 * @return
	 */
	public List<StudentSignRate> getOneStudentSignRateOfOneCourse(String result){
		List<StudentSignRate> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<StudentSignRate>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				StudentSignRate info = new StudentSignRate();
				//暂时只用了这两个字段
				
				info.setAllSignCount(job.getInt("allSignCount"));
				info.setCourse_id(job.getString("course_id"));
				info.setHave_sign(job.getInt("have_sign"));
				info.setRate(job.getInt("rate"));
				info.setStudent_id(job.getString("student_id"));
				info.setStudent_name(job.getString("student_name"));
				info.setStudent_username(job.getString("student_username"));
				
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 解析字符串，主要用于获得某一次签到未签到的学生信息。
	 * @param result
	 * @return   
	 */
	public List<StudentInfo> getUnsignedStudentsInfo(String result){
		List<StudentInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<StudentInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				StudentInfo info = new StudentInfo();
				//暂时只用了这两个字段
				
				info.setStudent_id(job.getInt("student_id"));
				info.setStudent_name(job.getString("student_name"));
				info.setStudent_username(job.getString("student_username"));
				
			
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
}











