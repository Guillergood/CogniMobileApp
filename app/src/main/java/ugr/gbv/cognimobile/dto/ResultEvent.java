package ugr.gbv.cognimobile.dto;

import java.util.List;

public class ResultEvent {
    private Long id;
    private long genericTimeStartTask;
    private long genericTimeHelp;
    private String specificATMAlreadyClickedButton;
    private List<Double> specificATMPoints;
    private String specificATMDistanceBetweenCircles;
    private String specificATMTimeBetweenClicks;
    private boolean genericSkippedTask;
    private long genericTimeEndTask;
    private long genericTimeNextTask;
    private String specificVSCubeStartDraw;
    private String specificVSCubeEndDraw;
    private List<Double> specificVSCubePoints;
    private String specificVSClockStartDraw;
    private String specificVSClockEndDraw;
    private List<Double> specificVSClockPoints;
    private String specificNamingCharacterChange;
    private String specificNamingStartWriting;
    private String specificNamingSubmitAnswer;
    private long genericTimeBeforeTask;
    private String specificMemoryScrollingList;
    private String specificMemorySettlingList;
    private String specificMemoryCharacterChange;
    private String specificMemoryStartWriting;
    private String specificMemorySubmitAnswer;
    private String specificAttentionNumbersItemPosition;
    private String specificAttentionNumbersItemPositionBackwards;
    private String specificAttentionNumbersStartWriting;
    private String specificAttentionNumbersSubmitAnswer;
    private String specificAttentionLettersSoundTimes;
    private String specificAttentionLettersTimeToAnswer;
    private String specificSubtractionCharacterChange;
    private String specificSubtractionStartWriting;
    private String specificSubtractionSubmitAnswer;
    private String specificSRCharacterChange;
    private String specificSRStartWriting;
    private String specificSRSubmitAnswer;
    private String specificFluencyScrollingList;
    private String specificFluencySettlingList;
    private String specificFluencyCharacterChange;
    private String specificFluencyStartWriting;
    private String specificFluencySubmitAnswer;
    private String specificAbstractionCharacterChange;
    private String specificAbstractionStartWriting;
    private String specificAbstractionSubmitAnswer;
    private String specificRecallScrollingList;
    private String specificRecallSettlingList;
    private String specificRecallCharacterChange;
    private String specificRecallStartWriting;
    private String specificRecallSubmitAnswer;
    private int specificRecallNumbersOfWords;
    private int specificRecallNumbersOfCorrectWords;
    private String specificOrientationCharacterChange;
    private String specificOrientationStartWriting;
    private String specificOrientationSubmitAnswer;

    public ResultEvent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getGenericTimeStartTask() {
        return genericTimeStartTask;
    }

    public void setGenericTimeStartTask(long genericTimeStartTask) {
        this.genericTimeStartTask = genericTimeStartTask;
    }

    public long getGenericTimeHelp() {
        return genericTimeHelp;
    }

    public void setGenericTimeHelp(long genericTimeHelp) {
        this.genericTimeHelp = genericTimeHelp;
    }

    public String getSpecificATMAlreadyClickedButton() {
        return specificATMAlreadyClickedButton;
    }

    public void setSpecificATMAlreadyClickedButton(String specificATMAlreadyClickedButton) {
        this.specificATMAlreadyClickedButton = specificATMAlreadyClickedButton;
    }

    public List<Double> getSpecificATMPoints() {
        return specificATMPoints;
    }

    public void setSpecificATMPoints(List<Double> specificATMPoints) {
        this.specificATMPoints = specificATMPoints;
    }

    public String getSpecificATMDistanceBetweenCircles() {
        return specificATMDistanceBetweenCircles;
    }

    public void setSpecificATMDistanceBetweenCircles(String specificATMDistanceBetweenCircles) {
        this.specificATMDistanceBetweenCircles = specificATMDistanceBetweenCircles;
    }

    public String getSpecificATMTimeBetweenClicks() {
        return specificATMTimeBetweenClicks;
    }

    public void setSpecificATMTimeBetweenClicks(String specificATMTimeBetweenClicks) {
        this.specificATMTimeBetweenClicks = specificATMTimeBetweenClicks;
    }

    public boolean isGenericSkippedTask() {
        return genericSkippedTask;
    }

    public void setGenericSkippedTask(boolean genericSkippedTask) {
        this.genericSkippedTask = genericSkippedTask;
    }

    public long getGenericTimeEndTask() {
        return genericTimeEndTask;
    }

    public void setGenericTimeEndTask(long genericTimeEndTask) {
        this.genericTimeEndTask = genericTimeEndTask;
    }

    public long getGenericTimeNextTask() {
        return genericTimeNextTask;
    }

    public void setGenericTimeNextTask(long genericTimeNextTask) {
        this.genericTimeNextTask = genericTimeNextTask;
    }

    public String getSpecificVSCubeStartDraw() {
        return specificVSCubeStartDraw;
    }

    public void setSpecificVSCubeStartDraw(String specificVSCubeStartDraw) {
        this.specificVSCubeStartDraw = specificVSCubeStartDraw;
    }

    public String getSpecificVSCubeEndDraw() {
        return specificVSCubeEndDraw;
    }

    public void setSpecificVSCubeEndDraw(String specificVSCubeEndDraw) {
        this.specificVSCubeEndDraw = specificVSCubeEndDraw;
    }

    public List<Double> getSpecificVSCubePoints() {
        return specificVSCubePoints;
    }

    public void setSpecificVSCubePoints(List<Double> specificVSCubePoints) {
        this.specificVSCubePoints = specificVSCubePoints;
    }

    public String getSpecificVSClockStartDraw() {
        return specificVSClockStartDraw;
    }

    public void setSpecificVSClockStartDraw(String specificVSClockStartDraw) {
        this.specificVSClockStartDraw = specificVSClockStartDraw;
    }

    public String getSpecificVSClockEndDraw() {
        return specificVSClockEndDraw;
    }

    public void setSpecificVSClockEndDraw(String specificVSClockEndDraw) {
        this.specificVSClockEndDraw = specificVSClockEndDraw;
    }

    public List<Double> getSpecificVSClockPoints() {
        return specificVSClockPoints;
    }

    public void setSpecificVSClockPoints(List<Double> specificVSClockPoints) {
        this.specificVSClockPoints = specificVSClockPoints;
    }

    public String getSpecificNamingCharacterChange() {
        return specificNamingCharacterChange;
    }

    public void setSpecificNamingCharacterChange(String specificNamingCharacterChange) {
        this.specificNamingCharacterChange = specificNamingCharacterChange;
    }

    public String getSpecificNamingStartWriting() {
        return specificNamingStartWriting;
    }

    public void setSpecificNamingStartWriting(String specificNamingStartWriting) {
        this.specificNamingStartWriting = specificNamingStartWriting;
    }

    public String getSpecificNamingSubmitAnswer() {
        return specificNamingSubmitAnswer;
    }

    public void setSpecificNamingSubmitAnswer(String specificNamingSubmitAnswer) {
        this.specificNamingSubmitAnswer = specificNamingSubmitAnswer;
    }

    public long getGenericTimeBeforeTask() {
        return genericTimeBeforeTask;
    }

    public void setGenericTimeBeforeTask(long genericTimeBeforeTask) {
        this.genericTimeBeforeTask = genericTimeBeforeTask;
    }

    public String getSpecificMemoryScrollingList() {
        return specificMemoryScrollingList;
    }

    public void setSpecificMemoryScrollingList(String specificMemoryScrollingList) {
        this.specificMemoryScrollingList = specificMemoryScrollingList;
    }

    public String getSpecificMemorySettlingList() {
        return specificMemorySettlingList;
    }

    public void setSpecificMemorySettlingList(String specificMemorySettlingList) {
        this.specificMemorySettlingList = specificMemorySettlingList;
    }

    public String getSpecificMemoryCharacterChange() {
        return specificMemoryCharacterChange;
    }

    public void setSpecificMemoryCharacterChange(String specificMemoryCharacterChange) {
        this.specificMemoryCharacterChange = specificMemoryCharacterChange;
    }

    public String getSpecificMemoryStartWriting() {
        return specificMemoryStartWriting;
    }

    public void setSpecificMemoryStartWriting(String specificMemoryStartWriting) {
        this.specificMemoryStartWriting = specificMemoryStartWriting;
    }

    public String getSpecificMemorySubmitAnswer() {
        return specificMemorySubmitAnswer;
    }

    public void setSpecificMemorySubmitAnswer(String specificMemorySubmitAnswer) {
        this.specificMemorySubmitAnswer = specificMemorySubmitAnswer;
    }

    public String getSpecificAttentionNumbersItemPosition() {
        return specificAttentionNumbersItemPosition;
    }

    public void setSpecificAttentionNumbersItemPosition(String specificAttentionNumbersItemPosition) {
        this.specificAttentionNumbersItemPosition = specificAttentionNumbersItemPosition;
    }

    public String getSpecificAttentionNumbersItemPositionBackwards() {
        return specificAttentionNumbersItemPositionBackwards;
    }

    public void setSpecificAttentionNumbersItemPositionBackwards(String specificAttentionNumbersItemPositionBackwards) {
        this.specificAttentionNumbersItemPositionBackwards = specificAttentionNumbersItemPositionBackwards;
    }

    public String getSpecificAttentionNumbersStartWriting() {
        return specificAttentionNumbersStartWriting;
    }

    public void setSpecificAttentionNumbersStartWriting(String specificAttentionNumbersStartWriting) {
        this.specificAttentionNumbersStartWriting = specificAttentionNumbersStartWriting;
    }

    public String getSpecificAttentionNumbersSubmitAnswer() {
        return specificAttentionNumbersSubmitAnswer;
    }

    public void setSpecificAttentionNumbersSubmitAnswer(String specificAttentionNumbersSubmitAnswer) {
        this.specificAttentionNumbersSubmitAnswer = specificAttentionNumbersSubmitAnswer;
    }

    public String getSpecificAttentionLettersSoundTimes() {
        return specificAttentionLettersSoundTimes;
    }

    public void setSpecificAttentionLettersSoundTimes(String specificAttentionLettersSoundTimes) {
        this.specificAttentionLettersSoundTimes = specificAttentionLettersSoundTimes;
    }

    public String getSpecificAttentionLettersTimeToAnswer() {
        return specificAttentionLettersTimeToAnswer;
    }

    public void setSpecificAttentionLettersTimeToAnswer(String specificAttentionLettersTimeToAnswer) {
        this.specificAttentionLettersTimeToAnswer = specificAttentionLettersTimeToAnswer;
    }

    public String getSpecificSubtractionCharacterChange() {
        return specificSubtractionCharacterChange;
    }

    public void setSpecificSubtractionCharacterChange(String specificSubtractionCharacterChange) {
        this.specificSubtractionCharacterChange = specificSubtractionCharacterChange;
    }

    public String getSpecificSubtractionStartWriting() {
        return specificSubtractionStartWriting;
    }

    public void setSpecificSubtractionStartWriting(String specificSubtractionStartWriting) {
        this.specificSubtractionStartWriting = specificSubtractionStartWriting;
    }

    public String getSpecificSubtractionSubmitAnswer() {
        return specificSubtractionSubmitAnswer;
    }

    public void setSpecificSubtractionSubmitAnswer(String specificSubtractionSubmitAnswer) {
        this.specificSubtractionSubmitAnswer = specificSubtractionSubmitAnswer;
    }

    public String getSpecificSRCharacterChange() {
        return specificSRCharacterChange;
    }

    public void setSpecificSRCharacterChange(String specificSRCharacterChange) {
        this.specificSRCharacterChange = specificSRCharacterChange;
    }

    public String getSpecificSRStartWriting() {
        return specificSRStartWriting;
    }

    public void setSpecificSRStartWriting(String specificSRStartWriting) {
        this.specificSRStartWriting = specificSRStartWriting;
    }

    public String getSpecificSRSubmitAnswer() {
        return specificSRSubmitAnswer;
    }

    public void setSpecificSRSubmitAnswer(String specificSRSubmitAnswer) {
        this.specificSRSubmitAnswer = specificSRSubmitAnswer;
    }

    public String getSpecificFluencyScrollingList() {
        return specificFluencyScrollingList;
    }

    public void setSpecificFluencyScrollingList(String specificFluencyScrollingList) {
        this.specificFluencyScrollingList = specificFluencyScrollingList;
    }

    public String getSpecificFluencySettlingList() {
        return specificFluencySettlingList;
    }

    public void setSpecificFluencySettlingList(String specificFluencySettlingList) {
        this.specificFluencySettlingList = specificFluencySettlingList;
    }

    public String getSpecificFluencyCharacterChange() {
        return specificFluencyCharacterChange;
    }

    public void setSpecificFluencyCharacterChange(String specificFluencyCharacterChange) {
        this.specificFluencyCharacterChange = specificFluencyCharacterChange;
    }

    public String getSpecificFluencyStartWriting() {
        return specificFluencyStartWriting;
    }

    public void setSpecificFluencyStartWriting(String specificFluencyStartWriting) {
        this.specificFluencyStartWriting = specificFluencyStartWriting;
    }

    public String getSpecificFluencySubmitAnswer() {
        return specificFluencySubmitAnswer;
    }

    public void setSpecificFluencySubmitAnswer(String specificFluencySubmitAnswer) {
        this.specificFluencySubmitAnswer = specificFluencySubmitAnswer;
    }

    public String getSpecificAbstractionCharacterChange() {
        return specificAbstractionCharacterChange;
    }

    public void setSpecificAbstractionCharacterChange(String specificAbstractionCharacterChange) {
        this.specificAbstractionCharacterChange = specificAbstractionCharacterChange;
    }

    public String getSpecificAbstractionStartWriting() {
        return specificAbstractionStartWriting;
    }

    public void setSpecificAbstractionStartWriting(String specificAbstractionStartWriting) {
        this.specificAbstractionStartWriting = specificAbstractionStartWriting;
    }

    public String getSpecificAbstractionSubmitAnswer() {
        return specificAbstractionSubmitAnswer;
    }

    public void setSpecificAbstractionSubmitAnswer(String specificAbstractionSubmitAnswer) {
        this.specificAbstractionSubmitAnswer = specificAbstractionSubmitAnswer;
    }

    public String getSpecificRecallScrollingList() {
        return specificRecallScrollingList;
    }

    public void setSpecificRecallScrollingList(String specificRecallScrollingList) {
        this.specificRecallScrollingList = specificRecallScrollingList;
    }

    public String getSpecificRecallSettlingList() {
        return specificRecallSettlingList;
    }

    public void setSpecificRecallSettlingList(String specificRecallSettlingList) {
        this.specificRecallSettlingList = specificRecallSettlingList;
    }

    public String getSpecificRecallCharacterChange() {
        return specificRecallCharacterChange;
    }

    public void setSpecificRecallCharacterChange(String specificRecallCharacterChange) {
        this.specificRecallCharacterChange = specificRecallCharacterChange;
    }

    public String getSpecificRecallStartWriting() {
        return specificRecallStartWriting;
    }

    public void setSpecificRecallStartWriting(String specificRecallStartWriting) {
        this.specificRecallStartWriting = specificRecallStartWriting;
    }

    public String getSpecificRecallSubmitAnswer() {
        return specificRecallSubmitAnswer;
    }

    public void setSpecificRecallSubmitAnswer(String specificRecallSubmitAnswer) {
        this.specificRecallSubmitAnswer = specificRecallSubmitAnswer;
    }

    public int getSpecificRecallNumbersOfWords() {
        return specificRecallNumbersOfWords;
    }

    public void setSpecificRecallNumbersOfWords(int specificRecallNumbersOfWords) {
        this.specificRecallNumbersOfWords = specificRecallNumbersOfWords;
    }

    public int getSpecificRecallNumbersOfCorrectWords() {
        return specificRecallNumbersOfCorrectWords;
    }

    public void setSpecificRecallNumbersOfCorrectWords(int specificRecallNumbersOfCorrectWords) {
        this.specificRecallNumbersOfCorrectWords = specificRecallNumbersOfCorrectWords;
    }

    public String getSpecificOrientationCharacterChange() {
        return specificOrientationCharacterChange;
    }

    public void setSpecificOrientationCharacterChange(String specificOrientationCharacterChange) {
        this.specificOrientationCharacterChange = specificOrientationCharacterChange;
    }

    public String getSpecificOrientationStartWriting() {
        return specificOrientationStartWriting;
    }

    public void setSpecificOrientationStartWriting(String specificOrientationStartWriting) {
        this.specificOrientationStartWriting = specificOrientationStartWriting;
    }

    public String getSpecificOrientationSubmitAnswer() {
        return specificOrientationSubmitAnswer;
    }

    public void setSpecificOrientationSubmitAnswer(String specificOrientationSubmitAnswer) {
        this.specificOrientationSubmitAnswer = specificOrientationSubmitAnswer;
    }
}
