import java.lang.StringBuilder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;

public class ANSIParser {

	private StringBuilder t;

	public ANSIParser() {
		t = new StringBuilder();
	}

	public String insertANSI(String s) {
		t.setLength(0);
		Pattern p = Pattern.compile("<\\$(\\w*?)>");
		Matcher m = p.matcher(s);
		String ms;
		while (m.find()) {
			ms = m.group(1);
			m.appendReplacement(t, stoANSI(ms));
		}
		m.appendTail(t);

		return t.toString();
	}

	private String stoANSI(String a) {
		String ansi = "\u001b[";
		switch (a) {
			case "r":
			case "reset":
				ansi += "0";
				break;
			case "fblack":
			case "fbl":
				ansi += "30";
				break;
			case "fred":
			case "fr":
				ansi += "31";
				break;
			case "fgreen":
			case "fg":
				ansi += "32";
				break;
			case "fyellow":
			case "fy":
				ansi += "33";
				break;
			case "fblue":
			case "fb":
				ansi += "34";
				break;
			case "fpurple":
			case "fviolet":
			case "fp":
			case "fv":
				ansi += "35";
				break;
			case "fcyan":
			case "fc":
				ansi += "36";
				break;
			case "fwhite":
			case "fw":
				ansi += "37";
				break;
			case "fgrey":
			case "fgray":
			case "fgy":
				ansi += "90";
				break;
			case "bblack":
			case "bbl":
				ansi += "40";
				break;
			case "bred":
			case "br":
				ansi += "41";
				break;
			case "bgreen":
			case "bg":
				ansi += "42";
				break;
			case "byellow":
			case "by":
				ansi += "43";
				break;
			case "bblue":
			case "bb":
				ansi += "44";
				break;
			case "bpurple":
			case "bviolet":
			case "bp":
			case "bv":
				ansi += "45";
				break;
			case "bcyan":
			case "bc":
				ansi += "46";
				break;
			case "bwhite":
			case "bw":
				ansi += "47";
				break;
			case "bgrey":
			case "bgray":
			case "bgr":
				ansi += "100";
				break;
			default:
				ansi += "0"; // by default reset
				break;
		}
		return ansi + "m";
	}
}
