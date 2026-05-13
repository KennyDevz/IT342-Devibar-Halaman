import api from '../../../core/api';


//PUT /api/users/profile
//Updates the user's first and last name.

export const updateProfile = (data) => api.put('/api/users/profile', data);

//PUT /api/users/password
//Changes the user's password given currentPassword + newPassword.

export const changePassword = (data) => api.put('/api/users/password', data);


//DELETE /api/users/me
//Permanently deletes the authenticated user's account.
export const deleteAccount = () => api.delete('/api/users/me');
