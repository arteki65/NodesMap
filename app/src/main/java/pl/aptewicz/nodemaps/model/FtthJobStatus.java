package pl.aptewicz.nodemaps.model;

public enum FtthJobStatus {
	NEW, IN_PROGRESS, DONE;


	@Override
	public String toString() {
		if(this.equals(NEW)) {
			return "NOWE";
		}
		if(this.equals(IN_PROGRESS)) {
			return "W TRAKCIE REALIZACJI";
		}
		if(this.equals(DONE)) {
			return "ZAKOŃCZONE";
		}
		return super.toString();
	}
}
