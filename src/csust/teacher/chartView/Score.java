package csust.teacher.chartView;

import java.io.Serializable;

/**
 * 记录一次签到，封装一次
 * 
 * @author U-anLA
 *
 */
public class Score implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String times;// 记录第几次
	private int score;// 就是某一次签到的到课率
	private String allow_sign_id;// 某一次的签到id

	
	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getAllow_sign_id() {
		return allow_sign_id;
	}

	public void setAllow_sign_id(String allow_sign_id) {
		this.allow_sign_id = allow_sign_id;
	}

	@Override
	public String toString() {
		return "Score [times=" + times + ", score=" + score
				+ ", allow_sign_id=" + allow_sign_id + "]";
	}

	

}
