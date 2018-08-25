package com.revature.dao;
import com.revature.entity.TfInterview;
import java.util.List;

public interface InterviewDao {
	 List<TfInterview> getInterviewsByAssociate(int associateId);
	 List<TfInterview> getAllInterviews();
	 boolean createInterview(TfInterview interview);
	 boolean updateInterview(	TfInterview interview);
	 TfInterview getInterviewById(int interviewId);
}