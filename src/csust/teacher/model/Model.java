package csust.teacher.model;

import java.util.List;

import android.os.Environment;
import csust.teacher.activity.ChatActivity;
import csust.teacher.info.CourseStudentListInfo;
import csust.teacher.info.UserInfo;

/**
 * 保存全局变量
 * 
 * @author anLA7856
 *
 */
public class Model {

	// public static String BASEHTTPURL = "http://120.76.146.248:8080";
	public static String BASEHTTPURL = "http://192.168.191.1:8989";
	public static String HTTPURL = BASEHTTPURL + "/Sign1.1/";

	public static String BASELOCATION = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			.getAbsolutePath();
	public static String LOCALSTORAGE = BASELOCATION + "/sign/";
	public static String REPORTDATALOCATION = LOCALSTORAGE + "download/";
	public static String UPLOADPIC = "teaUploadPic";
	public static String UPLOADNEWCOURSE = "uploadNewCourse";
	public static String TEADELETECOURSE = "teaDeleteCourse?";
	public static String TEAGETMYSIGNINGINFO = "teaGetMySigningInfo?";
	public static String CLOSESIGN = "closeSign?";
	public static String UPDATESIGNINFO = "updateSignInfo?";
	public static String GETSIGNSTUDENTINFO = "getSignStudentInfo?";
	public static String GETSIGNCOUNT = "getSignCount?";
	public static String CHANGESIGNSTATE = "changeSignState?";
	public static String GETCOURSESIGNINFOCOUNT = "getCourseSignInfoCount?";
	public static String GETTEACOURSE = "getTeaCourse?";
	public static String GETALLSIGNLISTOFCOURSE = "getAllSignListOfCourse?";
	public static String GETSTUDENTLISTCOURSERATE = "getStudentListCourseRate?";
	public static String TEAMODIFYPASSWORD = "teaModifyPassword?";
	public static String GETUNSIGNEDSTUDENTS = "getUnsignedStudents?";
	public static String ADDNEWSTUDENTSIGNSTATE = "addNewStudentSignState?";
	public static String GETSIGNEDREPORT = "getSignedReport?";
	public static String GETCOURSESTUDENTLIST = "getCourseStudentList?";
	public static String TEACHATMESSAGEADD = "teaChatMessageAdd?";
	// 可以和学生端一样
	public static String STUGETNEWCHATMESSAGE = "stuGetNewChatMessage?";
	// 可以和学生端一样
	public static String STUGETALLCHATMESSAGE = "stuGetAllChatMessage?";

	public static int INIT_COUNT = 15;
	// 注册用的url
	// 用post方式，所有不用问号
	public static String REGISTET = "teaAdd";
	public static String ADDCOMMENT = "addcomment.php";
	public static String LOGIN = "teaLogin";

	// 用于加载图片的。
	public static String USERHEADURL = BASEHTTPURL + "/Sign1.1/teaPic/";

	public static String USERREPORTURL = BASEHTTPURL + "/Sign1.1/xls/";

	public static boolean IMGFLAG = false;
	public static UserInfo MYUSERINFO = null;
	public static ChatActivity MYCHATACTIVITY = null;
	public static List<CourseStudentListInfo> LIST = null;

}
