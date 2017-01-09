package pl.aptewicz.nodemaps.model;

public class FtthRestApiError {

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String translate() {
		if (FtthRestApiExceptionConstants.SERVICEMAN_TOO_FAR_FROM_ISSUE_LOCATION.equals(code)) {
			return "Zbyt duża odległość od miejsca zgłoszenia. Aktualizacja statusu możliwa w "
					+ "promieniu 100 metrów od zgłoszenia.";
		}
		return "INTERNAL SERVER ERROR";
	}
}
