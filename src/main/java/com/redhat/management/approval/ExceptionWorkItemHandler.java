package com.redhat.management.approval;

import java.util.Map;
import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ExceptionWorkItemHandler implements WorkItemHandler {

  public static String exceptionParameterName = "jbpm.workitem.exception";
  public static String requestId = "RequestId";

  @Override
  public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    manager.abortWorkItem(workItem.getId());

  }

  @Override
  public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    Map<String, Object> resultMap = new HashMap<String, Object>();
    System.out.println("workItem: " + workItem.toString());
    Object item = workItem.getParameter("Error");

    if (item instanceof WorkItem) {
      WorkItem exceptionWorkItem = (WorkItem) item;
      handleException(exceptionWorkItem);
      resultMap.put("message", getErrorMessage(exceptionWorkItem));
    } else if (item instanceof String) {
      resultMap.put("message", item.toString());
    }

    manager.completeWorkItem(workItem.getId(), resultMap);
  }

  public void handleException(WorkItem workItem) {
    Map<String, Object> params = workItem.getParameters();
    String insightsRequestId = (String) params.get(requestId);
    Throwable throwable = (Throwable) params.get(exceptionParameterName);
    throwable.printStackTrace();

    Logger log = Logger.getLogger(insightsRequestId);

    log.error( "Handling exception caused by work item '" + workItem.getName() + "' (id: " + workItem.getId() + ")");
  }

  public String getErrorMessage(WorkItem workItem) {
    Map<String, Object> params = workItem.getParameters();

    return ((Throwable)params.get(exceptionParameterName)).getMessage();
  }

}
