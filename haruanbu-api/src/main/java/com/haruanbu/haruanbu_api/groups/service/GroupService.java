package com.haruanbu.haruanbu_api.groups.service;

import com.haruanbu.haruanbu_api.common.exception.ApiException;
import com.haruanbu.haruanbu_api.common.exception.ErrorCode;
import com.haruanbu.haruanbu_api.groups.domain.*;
import com.haruanbu.haruanbu_api.groups.dto.*;
import com.haruanbu.haruanbu_api.groups.repository.CareGroupMemberRepository;
import com.haruanbu.haruanbu_api.groups.repository.CareGroupRepository;
import com.haruanbu.haruanbu_api.groups.repository.GroupInviteRepository;
import com.haruanbu.haruanbu_api.users.domain.User;
import com.haruanbu.haruanbu_api.users.domain.UserRole;
import com.haruanbu.haruanbu_api.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GroupService{

    private final CareGroupRepository groupRepo;
    private final CareGroupMemberRepository memberRepo;
    private final GroupInviteRepository inviteRepo;
    private final UserRepository userRepo;

    private final SecureRandom random = new SecureRandom();
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LEN = 10;

     public GroupService(
        CareGroupRepository groupRepo,
        CareGroupMemberRepository memberRepo,
        GroupInviteRepository inviteRepo,
        UserRepository userRepo
    ) {
            this.groupRepo = groupRepo;
            this.memberRepo = memberRepo;
            this.inviteRepo = inviteRepo;
            this.userRepo = userRepo;
    }

    @Transactional
    public GroupResponse createGroup(UUID currentUserId, CreateGroupRequest req){
        User user = mustFindUser(currentUserId);
        mustBeGuardian(user);

        UUID groupId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        CareGroup group = new CareGroup(groupId, req.name(), currentUserId, now);
        groupRepo.save(group);

        // 생성자는 자동으로 GUARDIAN 멤버로 등록
        CareGroupMember member = new CareGroupMember(
                new CareGroupMemberId(groupId, currentUserId),
                GroupMemberRole.GUARDIAN,
                "{}",
                now
        );
        memberRepo.save(member);

        return new GroupResponse(groupId, group.getName());
    }


    public CreateInviteResponse createInvite(UUID currentUserId, UUID groupId){
        User user = mustFindUser(currentUserId);
        mustBeGuardian(user);
        mustBeGuardianOfGroup(currentUserId, groupId);

        String code = generateUniqueCode();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(24);

        GroupInvite invite = new GroupInvite(UUID.randomUUID(), groupId, currentUserId, code, expiresAt);
        inviteRepo.save(invite);

        return new CreateInviteResponse(code, expiresAt);
    }

    @Transactional
    public AcceptInviteResponse acceptInvite(UUID currentUserId, String code){
        User user = mustFindUser(currentUserId);
        if(user.getRole() != UserRole.SENIOR){
            throw new ApiException(ErrorCode.FORBIDDEN, "어르신만 초대코드를 수락할 수 있습니다");
        }

        GroupInvite invite = inviteRepo.findByCode(code)
                .orElseThrow(() -> new ApiException(ErrorCode.INVITE_NOT_FOUND, "초대코드를 찾을 수 없습니다."));

        OffsetDateTime now = OffsetDateTime.now();

        if(invite.getExpiresAt().isBefore(now)){
            throw new ApiException(ErrorCode.INVITE_EXPIRED, "초대코드 만료");
        }

        if (invite.isUsed()) {
            throw new ApiException(ErrorCode.INVITE_ALREADY_USED, "이미 사용된 초대코드");
        }

        UUID groupId = invite.getGroupId();
        
        // 이미 가입되어 있다면 성공 처리
        boolean already = memberRepo.existsByIdGroupIdAndIdUserId(groupId, currentUserId);
        if(!already){
            memberRepo.save(new CareGroupMember(
                    new CareGroupMemberId(groupId, currentUserId),
                    GroupMemberRole.SENIOR,
                    "{}",
                    now
            ));
        }

        invite.markUsed(currentUserId, now);
        inviteRepo.save(invite);

        return new AcceptInviteResponse(groupId, GroupMemberRole.SENIOR.name());
    }

    @Transactional
    public List<MemberResponse> getMembers(UUID currentUserId, UUID groupId){
         User user = mustFindUser(currentUserId);
         mustBeGuardian(user);
         mustBeGuardianOfGroup(currentUserId, groupId);

         return memberRepo.findByIdGroupId(groupId).stream()
                 .map(m -> new MemberResponse(m.getId().getUserId(), m.getMemberRole().name()))
                 .toList();
    }


    private User mustFindUser(UUID id){
         return userRepo.findById(id)
                 .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없음"));
    }

    private void mustBeGuardian(User user){
         if (user.getRole() != UserRole.GUARDIAN) {
             throw new ApiException(ErrorCode.FORBIDDEN, "보호자만 가능합니다");
         }
    }

    private void mustBeGuardianOfGroup(UUID userId, UUID groupId){
         // 그룹 존재 확인
        groupRepo.findById(groupId).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND, "존재하는 그룹 없음"));

        var memberOpt = memberRepo.findByIdGroupIdAndIdUserId(groupId, userId);
        if(memberOpt.isEmpty() || memberOpt.get().getMemberRole() != GroupMemberRole.GUARDIAN){
            throw new ApiException(ErrorCode.FORBIDDEN, "이 그룹을 볼 권한이 없습니다.");
        }
    }

    private String generateUniqueCode(){
         for (int i=0; i<10; i++){
             String code = randomCode(CODE_LEN);
             if (!inviteRepo.existsByCode(code)) return code;
         }

         throw new ApiException(ErrorCode.FORBIDDEN, "초대코드 생성에 실패했습니다.");
    }

    private String randomCode(int len){
         StringBuilder sb = new StringBuilder(len);
         for (int i=0; i<len; i++){
             int idx = random.nextInt(CODE_CHARS.length());
             sb.append(CODE_CHARS.charAt(idx));
         }
         return sb.toString();
    }







}






