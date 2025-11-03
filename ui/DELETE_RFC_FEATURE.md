# RFC Deletion Feature

## Overview
Added comprehensive RFC deletion functionality to `rfc-list.html` with proper validation, confirmation flow, and API integration.

## Features Implemented

### 1. **Delete Button in RFC Details Modal**
- Located in the modal footer
- Only visible when deletion is allowed
- Visual styling with danger (red) color scheme

### 2. **Access Control**
RFC can be deleted only if:
- Status is **NOT** `APPROVED`, `IMPLEMENTED`, or `REJECTED`
- User is either:
  - The RFC creator (requesterId matches current user ID), OR
  - Has `ADMIN` role

### 3. **Delete Confirmation Modal**
A dedicated confirmation dialog with:

#### Warning Message
- Clear indication that the action is irreversible
- List of data that will be deleted:
  - System and subsystem information
  - Change history and audit logs
  - All attached files
  - Comments and notes

#### RFC Information Display
- Shows the title of the RFC being deleted
- Styled as code/monospace for clarity

#### Confirmation Input
- User must type the exact RFC title to enable deletion
- Real-time validation as user types
- Delete button remains disabled until titles match exactly

### 4. **Deletion Flow**

```
1. User opens RFC details modal
2. If allowed, "Delete RFC" button is shown
3. User clicks "Delete RFC"
4. Confirmation modal opens with warning
5. User must type RFC title exactly
6. "Delete" button enables only when title matches
7. User clicks "Delete"
8. API call: DELETE /rfc/{id}
9. Success: RFC removed from list, toast notification shown
10. Modal closes automatically
```

### 5. **API Integration**

Following OpenAPI specification: `DELETE /rfc/{id}`

**Request:**
```javascript
DELETE /api/rfc/{id}
Headers:
  Authorization: Bearer <token>
```

**Response:**
- `204 No Content` - Success
- `400 Bad Request` - Validation error (e.g., RFC cannot be deleted due to status)
- `401 Unauthorized` - Not authenticated
- `404 Not Found` - RFC doesn't exist
- `500 Internal Server Error` - Server error

**Implementation:**
```javascript
const response = await fetch(`/api/rfc/${rfcId}`, {
    method: 'DELETE',
    headers: {
        'Authorization': 'Bearer ' + getAuthToken()
    }
});

if (response.status === 204) {
    // Success - show toast and update UI
}
```

### 6. **UI/UX Features**

#### Visual Feedback
- Loading overlay during API call
- Toast notification on success/error
- Smooth fade-out animation when removing RFC from table
- Disabled states for buttons during processing

#### Keyboard Support
- ESC key closes all modals (including delete confirmation)
- Standard modal overlay click to close

#### Error Handling
- Network errors caught and displayed
- API errors shown with meaningful messages
- Validation errors prevented at UI level

#### Responsive Design
- Modal works on mobile and desktop
- Touch-friendly button sizes
- Proper spacing and readability

### 7. **Mock Implementation**

For development/demo purposes, includes:
- Simulated API delay (1 second)
- Animated removal from table
- Pagination info update
- Empty state display when no RFCs remain

## CSS Classes Added

```css
.modal-footer           /* Footer section in RFC details modal */
.btn-danger            /* Red delete button */
.btn-secondary         /* Gray cancel button */
.delete-confirm-modal  /* Delete confirmation modal wrapper */
.delete-warning        /* Warning box with yellow background */
.delete-rfc-info       /* Info box showing RFC being deleted */
.delete-confirm-input  /* Input field styling */
```

## JavaScript Functions Added

```javascript
currentViewedRfc              // Global var storing current RFC
openDeleteConfirmModal()      // Opens delete confirmation
closeDeleteConfirmModal()     // Closes delete confirmation
confirmDeleteRfc()            // Executes deletion
```

## Testing Scenarios

### ✅ Can Delete
- RFC-001 (Status: NEW, Creator: Иванов И., Current User: Иванов И.)
- RFC-002 (Status: UNDER_REVIEW, Any RFC with current user as ADMIN)

### ❌ Cannot Delete
- RFC-003 (Status: APPROVED) - Button hidden
- RFC-004 (Status: IMPLEMENTED) - Button hidden
- RFC-005 (Status: REJECTED) - Button hidden
- Any RFC if current user is not creator AND not ADMIN

## Security Considerations

1. **Frontend validation** - UI prevents unauthorized attempts
2. **Backend validation required** - Server must verify:
   - User permissions (creator or admin)
   - RFC status (not approved/implemented/rejected)
   - RFC ownership

3. **Token-based authentication** - All API calls include Bearer token
4. **Audit logging recommended** - Server should log deletion events

## Future Enhancements

1. Soft delete option (mark as deleted but keep data)
2. Restore functionality for admins
3. Bulk delete for multiple RFCs
4. Deletion reason field
5. Email notifications to stakeholders
6. Recycle bin / trash folder

## Related Files

- `ui/rfc-list.html` - Main implementation
- `backend/rfc-service/src/main/resources/openapi/api/rfc/RfcById.yaml` - API spec (DELETE operation)

## Notes

- The delete button appears in the RFC details modal footer
- Modal footer is hidden by default and shown only when deletion is allowed
- The confirmation requires exact title match to prevent accidental deletions
- All text is in Russian to match the application locale

