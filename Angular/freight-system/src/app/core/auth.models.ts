export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthSession {
  token: string;
  userId: string;
  email: string;
  fullName: string;
  userType: string;
  expiresIn: number;
  tokenType: string;
  roles: string[];
  authorities: string[];
  menuIds: string[];
}

export interface TokenValidationResponse {
  valid: boolean;
}

