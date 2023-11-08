package com.hst.business

import com.hst.fsp.FspUserInfo
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import com.hst.business.FspEvents.InviteIncome
import com.hst.fsp.WhiteBoardInfo

open class FspEvents {
    class LoginResult(var isSuccess: Boolean, var desc: String)
    class JoinGroupResult(var isSuccess: Boolean, var desc: String)
    class LeaveGroupResult(var isSuccess: Boolean, var desc: String)
    class RemoteVideoEvent(var userid: String, var videoid: String, var eventtype: Int)
    class RemoteAudioEvent(var userid: String, var eventtype: Int)
    class RemoteUserEvent(var userid: String, var eventtype: Int)
    class RefreshUserStatusFinished(
        var isSuccess: Boolean,
        var requestId: Int,
        var infos: Array<FspUserInfo>,
        var desc: String
    )

    class InviteIncome : Parcelable {
        var inviterUserId: String?
        var inviteId: Int
        var groupId: String?
        var desc: String?

        constructor(inviterUserId: String?, inviteId: Int, groupId: String?, desc: String?) {
            this.inviterUserId = inviterUserId
            this.inviteId = inviteId
            this.groupId = groupId
            this.desc = desc
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(inviterUserId)
            dest.writeInt(inviteId)
            dest.writeString(groupId)
            dest.writeString(desc)
        }

        protected constructor(`in`: Parcel) {
            inviterUserId = `in`.readString()
            inviteId = `in`.readInt()
            groupId = `in`.readString()
            desc = `in`.readString()
        }

        companion object {
            @JvmField
            val CREATOR: Creator<InviteIncome?> = object : Creator<InviteIncome?> {
                override fun createFromParcel(source: Parcel): InviteIncome? {
                    return InviteIncome(source)
                }

                override fun newArray(size: Int): Array<InviteIncome?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    class ChatMsgItem(
        var isGroupMsg: Boolean,
        var srcUserId: String,
        var msgId: Int,
        var msg: String,
        var isMyselfMsg: Boolean
    )

    class WhiteBoardPublishEvent(var isStop: Boolean, var boardId: String, var boardName: String)
    class WhiteBoardInfoUpdateEvent(var boardId: String, var info: WhiteBoardInfo)
}