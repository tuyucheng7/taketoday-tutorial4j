package cn.tuyucheng.taketoday.mongodb.events;


import cn.tuyucheng.taketoday.mongodb.services.SequenceGeneratorService;
import cn.tuyucheng.taketoday.mongodb.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class UserModelListener extends AbstractMongoEventListener<User> {

	private SequenceGeneratorService sequenceGenerator;

	@Autowired
	public UserModelListener(SequenceGeneratorService sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<User> event) {
		if (event.getSource().getId() < 1) {
			event.getSource().setId(sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
		}
	}
}