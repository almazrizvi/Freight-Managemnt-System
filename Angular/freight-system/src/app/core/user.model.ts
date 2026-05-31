export interface User {
  id?: string; // UUID
  email: string;
  fullName: string;
  passwordHash?: string;
  userType: 'INTERNAL' | 'CUSTOMER' | 'DRIVER';
  isActive: boolean;
  deletedAt?: string;
  deletedBy?: string;
  createdAt?: string;
}
