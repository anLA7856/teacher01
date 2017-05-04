package csust.teacher.model;

import java.util.List;

import android.os.Environment;
import csust.teacher.activity.ChatActivity;
import csust.teacher.info.CourseStudentListInfo;
import csust.teacher.info.UserInfo;



public class Model {


	//public static String BASEHTTPURL = "http://115.29.50.213:8989";
	public static String BASEHTTPURL = "http://10.129.92.21:8989";
	public static String HTTPURL = BASEHTTPURL+"/Sign1.1/";

	public static String BASELOCATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
	public static String LOCALSTORAGE = BASELOCATION+"/sign/";
	public static String REPORTDATALOCATION = LOCALSTORAGE+"download/";
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
	//可以和学生端一样
	public static String STUGETNEWCHATMESSAGE = "stuGetNewChatMessage?";
	//可以和学生端一样
	public static String STUGETALLCHATMESSAGE = "stuGetAllChatMessage?";
	
	
	public static int INIT_COUNT = 15;
	//注册用的url
	//用post方式，所有不用问号
	public static String REGISTET = "teaAdd";
	public static String ADDCOMMENT = "addcomment.php";
	public static String LOGIN = "teaLogin";

	//用于加载图片的。
	public static String USERHEADURL = BASEHTTPURL+"/Sign1.1/teaPic/";

	public static String USERREPORTURL = BASEHTTPURL+"/Sign1.1/xls/";

	

	public static boolean IMGFLAG = false;
	public static UserInfo MYUSERINFO = null;
	public static ChatActivity MYCHATACTIVITY = null;
	public static List<CourseStudentListInfo> LIST = null;
	// APP客服KEY
	public static String APPKEY = "f241ebf4d4a1e1dfae6f1a3e49aad2f5";
	/** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
	public static final String APP_KEY = "3987368837";

	/**
	 * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
	 * 
	 * <p>
	 * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响， 但是没有定义将无法使用 SDK 认证登录。
	 * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
	 * </p>
	 */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	/**
	 * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
	 * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
	 * 
	 * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
	 * 
	 * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
	 * 
	 * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
	 * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
	 */
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
}
