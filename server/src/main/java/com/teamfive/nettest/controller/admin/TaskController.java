package com.teamfive.nettest.controller.admin;


import com.teamfive.nettest.base.BaseApiController;
import com.teamfive.nettest.base.RestResponse;
import com.teamfive.nettest.domain.TaskExam;
import com.teamfive.nettest.service.TaskExamService;
import com.teamfive.nettest.utility.DateTimeUtil;
import com.teamfive.nettest.utility.PageInfoHelper;
import com.teamfive.nettest.viewmodel.admin.task.TaskPageRequestVM;
import com.teamfive.nettest.viewmodel.admin.task.TaskPageResponseVM;
import com.teamfive.nettest.viewmodel.admin.task.TaskRequestVM;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("AdminTaskController")
@RequestMapping(value = "/api/admin/task")
public class TaskController extends BaseApiController {

    private final TaskExamService taskExamService;

    @Autowired
    public TaskController(TaskExamService taskExamService) {
        this.taskExamService = taskExamService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<TaskPageResponseVM>> pageList(@RequestBody TaskPageRequestVM model) {
        PageInfo<TaskExam> pageInfo = taskExamService.page(model);
        PageInfo<TaskPageResponseVM> page = PageInfoHelper.copyMap(pageInfo, m -> {
            TaskPageResponseVM vm = modelMapper.map(m, TaskPageResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(m.getCreateTime()));
            return vm;
        });
        return RestResponse.ok(page);
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public RestResponse edit(@RequestBody @Valid TaskRequestVM model) {
        taskExamService.edit(model, getCurrentUser());
        TaskRequestVM vm = taskExamService.taskExamToVM(model.getId());
        return RestResponse.ok(vm);
    }


    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<TaskRequestVM> select(@PathVariable Integer id) {
        TaskRequestVM vm = taskExamService.taskExamToVM(id);
        return RestResponse.ok(vm);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse delete(@PathVariable Integer id) {
        TaskExam taskExam = taskExamService.selectById(id);
        taskExam.setDeleted(true);
        taskExamService.updateByIdFilter(taskExam);
        return RestResponse.ok();
    }
}