package cn.tuyucheng.taketoday.idd;

import java.util.List;

public interface HelpRequestService {
	HelpRequestDTO createHelpRequest(CreateHelpRequestDTO createHelpRequestDTO);

	List<HelpRequestDTO> findAllByStatus(HelpRequestStatus status);

	HelpRequestDTO updateHelpRequest(UpdateHelpRequestDTO updateHelpRequestDTO);
}