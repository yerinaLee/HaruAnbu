package com.haruanbu.haruanbu_api.groups.controller;

import com.haruanbu.haruanbu_api.common.web.CurrentUserId;
import com.haruanbu.haruanbu_api.groups.dto.*;
import com.haruanbu.haruanbu_api.groups.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService){
        this.groupService = groupService;
    }

    // 1) 그룹 생성 - 보호자
    @PostMapping("/groups")
    public GroupResponse createGroup(
            @CurrentUserId UUID userId,
            @RequestBody @Valid CreateGroupRequest request
    ){
        return groupService.createGroup(userId, request);
    }

    // 2) 초대코드 생성 - 보호자
    @PostMapping("/groups/{groupId}/invites")
    public CreateInviteResponse createInvite(
            @CurrentUserId UUID userId,
            @PathVariable UUID groupId
    ){
        return groupService.createInvite(userId, groupId);
    }

    // 3) 초대코드 수락 - 어르신
    @PostMapping("/invites/{code}/accept")
    public AcceptInviteResponse acceptInvite(
            @CurrentUserId UUID userId,
            @PathVariable String code
    ){
        return groupService.acceptInvite(userId, code);
    }

    // 4) 멤버 조회 - 보호자
    @GetMapping("/groups/{groupId}/members")
    public List<MemberResponse> members(
            @CurrentUserId UUID userId,
            @PathVariable UUID groupId
    ){
        return groupService.getMembers(userId, groupId);
    }


}
