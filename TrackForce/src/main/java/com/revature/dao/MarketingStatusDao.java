package com.revature.dao;
import com.revature.entity.TfMarketingStatus;
import java.util.List;

public interface MarketingStatusDao {
	List<TfMarketingStatus> getAllMarketingStatuses();
	TfMarketingStatus getMarketingStatusById(int id);
}