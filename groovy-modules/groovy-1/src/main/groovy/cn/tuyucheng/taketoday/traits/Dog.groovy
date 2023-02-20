package cn.tuyucheng.taketoday.traits

class Dog implements WalkingTrait, SpeakingTrait {

	String speakAndWalk() {
		WalkingTrait.super.speakAndWalk()
	}
}