teammates.storage.entity.Account {email}, teammates.storage.entity.AdminEmail {addressReceiver,subject,content}, teammates.storage.entity.CourseStudent {registrationKey,email}, teammates.storage.entity.FeedbackResponse {giverEmail}, teammates.storage.entity.FeedbackResponseComment {giverEmail,lastEditorEmail}, teammates.storage.entity.Instructor {email,registrationKey}, teammates.storage.entity.StudentProfile {email,googleId}, teammates.common.datatransfer.InstructorAttributes {googleId,email}, teammates.common.datatransfer.AccountAttributes {googleId,email}, teammates.common.datatransfer.StudentProfileAttributes {email,googleId}, teammates.common.datatransfer.AdminEmailAttributes {content}, teammates.common.datatransfer.CommentAttributes {lastEditorEmail}, teammates.common.datatransfer.FeedbackQuestionAttributes {creatorEmail}, teammates.common.datatransfer.FeedbackResponseCommentAttributes {giverEmail,lastEditorEmail}, teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle {instructorEmails}, teammates.common.datatransfer.FeedbackSessionAttributes {creatorEmail}, teammates.common.datatransfer.StudentEnrollDetails {email}, teammates.common.util.EmailWrapper {senderEmail,content}, teammates.common.datatransfer.FeedbackResponseAttributes {giver,recipient}, teammates.common.datatransfer.StudentAttributes {email}
 noflow GUI
	where GUI = {doGet,doPost}