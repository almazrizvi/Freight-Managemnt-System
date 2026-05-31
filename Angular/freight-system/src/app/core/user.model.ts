export interface User {
  id?: string; // UUID
  email: string;
  fullName: string;
  password?: string;
  userType: 'INTERNAL' | 'CUSTOMER' | 'DRIVER';
  isActive: boolean;
  roleCodes?: string[];
  authorities?: string[];
  menuIds?: string[];
  deletedAt?: string;
  deletedBy?: string;
  createdAt?: string;
}
