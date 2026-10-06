import React, { useState, useMemo } from 'react';
import { 
  GraduationCap, 
  Users, 
  BookOpen, 
  DollarSign, 
  CreditCard, 
  AlertCircle, 
  Search, 
  Plus, 
  Download, 
  CheckCircle, 
  Phone, 
  Laptop, 
  TrendingUp, 
  TrendingDown,
  ShieldCheck, 
  ChevronRight,
  ChevronDown,
  ChevronUp,
  Receipt,
  PieChart,
  Printer,
  Calendar,
  History,
  FileText
} from 'lucide-react';

interface Course {
  id: number;
  name: string;
  duration: string;
  totalFee: number;
  instructor: string;
  category: string;
}

interface Student {
  id: number;
  rollNo: string;
  name: string;
  fatherName: string;
  phone: string;
  courseName: string;
  totalFee: number;
  paidFee: number;
  remainingFee: number;
  admissionDate: string;
}

interface FeeReceipt {
  receiptNo: string;
  studentId: number;
  studentName: string;
  fatherName: string;
  rollNo: string;
  courseName: string;
  totalFee: number;
  previousPaid: number;
  amountPaid: number;
  newRemaining: number;
  feeMonth: string;
  isPreviousMonth: boolean;
  paymentMethod: string;
  paymentDate: string;
}

interface Expense {
  id: number;
  title: string;
  category: string;
  amount: number;
  date: string;
}

const INITIAL_EXPENSES: Expense[] = [
  { id: 1, title: 'Main Campus Building Rent', category: 'Rent', amount: 60000, date: '2026-10-01' },
  { id: 2, title: 'Faculty & Instructor Salaries', category: 'Salaries', amount: 55000, date: '2026-10-01' },
  { id: 3, title: 'Commercial Electricity & Generator Fuel', category: 'Utilities', amount: 20000, date: '2026-10-02' },
  { id: 4, title: 'Meta & Google Admission Ads', category: 'Marketing', amount: 15000, date: '2026-10-03' },
  { id: 5, title: 'Dedicated Fiber Internet & Software', category: 'Technology', amount: 10000, date: '2026-10-04' }
];

const INITIAL_COURSES: Course[] = [
  { id: 1, name: 'CIT', duration: '3 Months', totalFee: 15000, instructor: 'Sir Tariq Mehmood', category: 'Information Technology' },
  { id: 2, name: 'Trading', duration: '3 Months', totalFee: 25000, instructor: 'Sir Bilal Ahmed', category: 'Financial Markets' },
  { id: 3, name: 'Spoken English', duration: '2 Months', totalFee: 12000, instructor: "Ma'am Ayesha Khan", category: 'Languages' },
  { id: 4, name: 'Graphic Designing', duration: '3 Months', totalFee: 18000, instructor: 'Sir Daniyal Ali', category: 'Creative Arts' },
  { id: 5, name: 'DIT', duration: '1 Year', totalFee: 45000, instructor: 'Prof. Al Ghazi', category: 'Diploma' },
  { id: 6, name: 'Digital Marketing', duration: '3 Months', totalFee: 20000, instructor: 'Sir Hamza Raza', category: 'Marketing' }
];

const INITIAL_STUDENTS: Student[] = [
  { id: 1, rollNo: 'AG-CIT-001', name: 'Muhammad Ali', fatherName: 'Ghulam Qadir', phone: '03001234567', courseName: 'CIT', totalFee: 15000, paidFee: 10000, remainingFee: 5000, admissionDate: '2026-09-01' },
  { id: 2, rollNo: 'AG-CIT-002', name: 'Usman Ghani', fatherName: 'Muhammad Tariq', phone: '03129876543', courseName: 'CIT', totalFee: 15000, paidFee: 15000, remainingFee: 0, admissionDate: '2026-09-02' },
  { id: 3, rollNo: 'AG-TR-001', name: 'Zeeshan Khan', fatherName: 'Abdul Rehman', phone: '03335551234', courseName: 'Trading', totalFee: 25000, paidFee: 15000, remainingFee: 10000, admissionDate: '2026-09-05' },
  { id: 4, rollNo: 'AG-TR-002', name: 'Hamza Farooq', fatherName: 'Farooq Ahmed', phone: '03457788990', courseName: 'Trading', totalFee: 25000, paidFee: 25000, remainingFee: 0, admissionDate: '2026-09-06' },
  { id: 5, rollNo: 'AG-ENG-001', name: 'Fatima Noor', fatherName: 'Muhammad Asif', phone: '03214455667', courseName: 'Spoken English', totalFee: 12000, paidFee: 6000, remainingFee: 6000, admissionDate: '2026-09-10' }
];

export default function App() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'students' | 'fees' | 'expenses' | 'courses' | 'apk'>('dashboard');
  const [selectedCourseFilter, setSelectedCourseFilter] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [students, setStudents] = useState<Student[]>(INITIAL_STUDENTS);
  const [courses] = useState<Course[]>(INITIAL_COURSES);
  const [expenses, setExpenses] = useState<Expense[]>(INITIAL_EXPENSES);
  const [collapsedCourses, setCollapsedCourses] = useState<{ [key: string]: boolean }>({});
  
  // New Admission Modal State
  const [showAddModal, setShowAddModal] = useState(false);
  const [newStudentName, setNewStudentName] = useState('');
  const [newFatherName, setNewFatherName] = useState('');
  const [newPhone, setNewPhone] = useState('');
  const [newCourseName, setNewCourseName] = useState('CIT');
  const [newInitialPaid, setNewInitialPaid] = useState('');

  // Collect Fee Modal State
  const [selectedStudentForFee, setSelectedStudentForFee] = useState<Student | null>(null);
  const [feeAmountToPay, setFeeAmountToPay] = useState('');
  const [selectedFeeMonth, setSelectedFeeMonth] = useState('October 2026');
  const [paymentMethod, setPaymentMethod] = useState('Cash');
  const [generatedReceipt, setGeneratedReceipt] = useState<FeeReceipt | null>(null);

  // New Expense Modal State
  const [showExpenseModal, setShowExpenseModal] = useState(false);
  const [newExpenseTitle, setNewExpenseTitle] = useState('');
  const [newExpenseCategory, setNewExpenseCategory] = useState('Rent');
  const [newExpenseAmount, setNewExpenseAmount] = useState('');

  // Toggle Collapse for a course in grouped view
  const toggleCourseCollapse = (courseName: string) => {
    setCollapsedCourses(prev => ({
      ...prev,
      [courseName]: !prev[courseName]
    }));
  };

  // Calculations
  const totalRevenue = useMemo(() => students.reduce((acc, s) => acc + s.paidFee, 0), [students]);
  const totalPending = useMemo(() => students.reduce((acc, s) => acc + s.remainingFee, 0), [students]);
  const totalExpenses = useMemo(() => expenses.reduce((acc, e) => acc + e.amount, 0), [expenses]);
  const netBalance = useMemo(() => totalRevenue - totalExpenses, [totalRevenue, totalExpenses]);
  const recoveryRate = useMemo(() => {
    const totalTarget = totalRevenue + totalPending;
    return totalTarget > 0 ? (totalRevenue / totalTarget) * 100 : 0;
  }, [totalRevenue, totalPending]);

  // Course summaries (CIT, Trading, etc. segregated)
  const courseSummaries = useMemo(() => {
    const map = new Map<string, { count: number; totalFee: number; paidFee: number; remainingFee: number }>();
    courses.forEach(c => {
      map.set(c.name, { count: 0, totalFee: 0, paidFee: 0, remainingFee: 0 });
    });

    students.forEach(s => {
      const existing = map.get(s.courseName) || { count: 0, totalFee: 0, paidFee: 0, remainingFee: 0 };
      existing.count += 1;
      existing.totalFee += s.totalFee;
      existing.paidFee += s.paidFee;
      existing.remainingFee += s.remainingFee;
      map.set(s.courseName, existing);
    });

    return Array.from(map.entries()).map(([courseName, stats]) => ({
      courseName,
      ...stats
    }));
  }, [courses, students]);

  // Filtered students
  const filteredStudents = useMemo(() => {
    return students.filter(s => {
      const matchCourse = !selectedCourseFilter || s.courseName.toLowerCase() === selectedCourseFilter.toLowerCase();
      const matchQuery = !searchQuery || 
        s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        s.rollNo.toLowerCase().includes(searchQuery.toLowerCase()) ||
        s.phone.includes(searchQuery) ||
        s.courseName.toLowerCase().includes(searchQuery.toLowerCase());
      return matchCourse && matchQuery;
    });
  }, [students, selectedCourseFilter, searchQuery]);

  // Grouped students
  const groupedStudents = useMemo(() => {
    const groups: { [key: string]: Student[] } = {};
    filteredStudents.forEach(s => {
      if (!groups[s.courseName]) groups[s.courseName] = [];
      groups[s.courseName].push(s);
    });
    return groups;
  }, [filteredStudents]);

  const handleAddStudent = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newStudentName || !newPhone) return;

    const matchedCourse = courses.find(c => c.name === newCourseName) || courses[0];
    const initialPaidNum = parseFloat(newInitialPaid) || 0;
    const remaining = Math.max(0, matchedCourse.totalFee - initialPaidNum);

    const newStudent: Student = {
      id: Date.now(),
      rollNo: `AG-${newCourseName.substring(0, 3).toUpperCase()}-${String(students.length + 1).padStart(3, '0')}`,
      name: newStudentName,
      fatherName: newFatherName || 'Guardian',
      phone: newPhone,
      courseName: newCourseName,
      totalFee: matchedCourse.totalFee,
      paidFee: initialPaidNum,
      remainingFee: remaining,
      admissionDate: new Date().toISOString().split('T')[0]
    };

    setStudents([newStudent, ...students]);
    setShowAddModal(false);
    setNewStudentName('');
    setNewFatherName('');
    setNewPhone('');
    setNewInitialPaid('');
  };

  const handlePayFee = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedStudentForFee) return;

    const amount = parseFloat(feeAmountToPay) || 0;
    if (amount <= 0) return;

    const isPrev = selectedFeeMonth !== 'October 2026';
    const newPaid = selectedStudentForFee.paidFee + amount;
    const newRemaining = Math.max(0, selectedStudentForFee.totalFee - newPaid);

    const receipt: FeeReceipt = {
      receiptNo: `REC-2026-${String(Math.floor(1000 + Math.random() * 9000))}`,
      studentId: selectedStudentForFee.id,
      studentName: selectedStudentForFee.name,
      fatherName: selectedStudentForFee.fatherName,
      rollNo: selectedStudentForFee.rollNo,
      courseName: selectedStudentForFee.courseName,
      totalFee: selectedStudentForFee.totalFee,
      previousPaid: selectedStudentForFee.paidFee,
      amountPaid: amount,
      newRemaining: newRemaining,
      feeMonth: selectedFeeMonth,
      isPreviousMonth: isPrev,
      paymentMethod: paymentMethod,
      paymentDate: new Date().toLocaleString('en-US', { dateStyle: 'medium', timeStyle: 'short' })
    };

    setStudents(prev => prev.map(s => {
      if (s.id === selectedStudentForFee.id) {
        return {
          ...s,
          paidFee: newPaid,
          remainingFee: newRemaining
        };
      }
      return s;
    }));

    setSelectedStudentForFee(null);
    setFeeAmountToPay('');
    setGeneratedReceipt(receipt);
  };

  const handleAddExpense = (e: React.FormEvent) => {
    e.preventDefault();
    const amountNum = parseFloat(newExpenseAmount) || 0;
    if (!newExpenseTitle || amountNum <= 0) return;

    const newExp: Expense = {
      id: Date.now(),
      title: newExpenseTitle,
      category: newExpenseCategory,
      amount: amountNum,
      date: new Date().toISOString().split('T')[0]
    };

    setExpenses([newExp, ...expenses]);
    setShowExpenseModal(false);
    setNewExpenseTitle('');
    setNewExpenseAmount('');
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#0B132B', color: '#F8F9FA' }}>
      {/* Top Navigation Bar */}
      <header style={{
        backgroundColor: '#1C2541',
        borderBottom: '1px solid #3A506B',
        padding: '12px 24px',
        position: 'sticky',
        top: 0,
        zIndex: 50,
        boxShadow: '0 4px 12px rgba(0,0,0,0.3)'
      }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <img 
              src="/app-icon.png" 
              alt="Al Ghazi Digital Institute Emblem" 
              style={{
                width: '44px',
                height: '44px',
                borderRadius: '50%',
                border: '2px solid #D4AF37',
                objectFit: 'cover',
                boxShadow: '0 0 10px rgba(212, 175, 55, 0.3)'
              }}
              onError={(e) => {
                // fallback if image not loaded yet
                (e.target as HTMLElement).style.display = 'none';
              }}
            />
            <div>
              <h1 style={{ fontSize: '18px', fontWeight: 800, color: '#D4AF37', letterSpacing: '0.5px' }}>
                AL GHAZI DIGITAL INSTITUTE
              </h1>
              <p style={{ fontSize: '11px', color: '#94A3B8', fontWeight: 600 }}>
                LEARN • PRACTICE • GROW — Official Portal
              </p>
            </div>
          </div>

          {/* Navigation Links */}
          <nav style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
            {[
              { id: 'dashboard', label: 'Dashboard', icon: BookOpen },
              { id: 'students', label: 'Students Directory', icon: Users },
              { id: 'fees', label: 'Fee Management & Receipts', icon: DollarSign },
              { id: 'expenses', label: 'Expenses & Smart Charts', icon: TrendingDown },
              { id: 'courses', label: 'Courses', icon: Laptop },
              { id: 'apk', label: 'Download Android APK', icon: Download }
            ].map(tab => {
              const Icon = tab.icon;
              const isSelected = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    padding: '8px 14px',
                    borderRadius: '8px',
                    fontSize: '13px',
                    fontWeight: isSelected ? 700 : 500,
                    backgroundColor: isSelected ? '#D4AF37' : '#2D3A5D',
                    color: isSelected ? '#0B132B' : '#F8F9FA',
                    border: isSelected ? '1px solid #F3E5AB' : '1px solid #3A506B'
                  }}
                >
                  <Icon size={15} />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </nav>
        </div>
      </header>

      {/* Main Content Area */}
      <main style={{ maxWidth: '1200px', margin: '0 auto', padding: '24px 16px' }}>
        
        {/* ========================================================= */}
        {/* TAB 1: DASHBOARD */}
        {/* ========================================================= */}
        {activeTab === 'dashboard' && (
          <div>
            {/* Hero Banner */}
            <div style={{
              background: 'linear-gradient(135deg, #1C2541 0%, #2D3A5D 100%)',
              border: '1px solid #D4AF37',
              borderRadius: '16px',
              padding: '24px',
              marginBottom: '24px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              flexWrap: 'wrap',
              gap: '16px'
            }}>
              <div>
                <span style={{ 
                  backgroundColor: 'rgba(212, 175, 55, 0.2)', 
                  color: '#D4AF37', 
                  fontSize: '11px', 
                  fontWeight: 700, 
                  padding: '4px 10px', 
                  borderRadius: '20px',
                  textTransform: 'uppercase'
                }}>
                  Academic Year 2026-2027
                </span>
                <h2 style={{ fontSize: '24px', fontWeight: 800, marginTop: '8px', color: '#F8F9FA' }}>
                  Welcome to Al Ghazi Management System
                </h2>
                <p style={{ color: '#94A3B8', fontSize: '13px', marginTop: '4px', maxWidth: '600px' }}>
                  Full course-wise student segregation, automated fee tracking, and direct access to Android APK and online student records.
                </p>
              </div>

              <div style={{ display: 'flex', gap: '10px' }}>
                <button
                  onClick={() => setShowAddModal(true)}
                  style={{
                    backgroundColor: '#D4AF37',
                    color: '#0B132B',
                    border: 'none',
                    padding: '10px 18px',
                    borderRadius: '10px',
                    fontWeight: 700,
                    fontSize: '13px',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px'
                  }}
                >
                  <Plus size={16} />
                  <span>+ New Admission</span>
                </button>
                <button
                  onClick={() => setActiveTab('apk')}
                  style={{
                    backgroundColor: 'rgba(16, 185, 129, 0.2)',
                    color: '#10B981',
                    border: '1px solid #10B981',
                    padding: '10px 18px',
                    borderRadius: '10px',
                    fontWeight: 700,
                    fontSize: '13px',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px'
                  }}
                >
                  <Download size={16} />
                  <span>Get Android APK</span>
                </button>
              </div>
            </div>

            {/* Quick Stats Grid */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '16px', marginBottom: '24px' }}>
              <div style={{ backgroundColor: '#1C2541', border: '1px solid #3A506B', borderRadius: '12px', padding: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', color: '#94A3B8', fontWeight: 600 }}>Total Students Enrolled</span>
                  <Users size={18} color="#38BDF8" />
                </div>
                <div style={{ fontSize: '26px', fontWeight: 800, marginTop: '8px', color: '#38BDF8' }}>
                  {students.length}
                </div>
                <span style={{ fontSize: '11px', color: '#94A3B8' }}>Active Students</span>
              </div>

              <div style={{ backgroundColor: '#1C2541', border: '1px solid #3A506B', borderRadius: '12px', padding: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', color: '#94A3B8', fontWeight: 600 }}>Active Courses</span>
                  <BookOpen size={18} color="#D4AF37" />
                </div>
                <div style={{ fontSize: '26px', fontWeight: 800, marginTop: '8px', color: '#D4AF37' }}>
                  {courses.length}
                </div>
                <span style={{ fontSize: '11px', color: '#94A3B8' }}>CIT, Trading, English, etc.</span>
              </div>

              <div style={{ backgroundColor: '#1C2541', border: '1px solid #3A506B', borderRadius: '12px', padding: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', color: '#94A3B8', fontWeight: 600 }}>Fee Collected (وصول)</span>
                  <CheckCircle size={18} color="#10B981" />
                </div>
                <div style={{ fontSize: '24px', fontWeight: 800, marginTop: '8px', color: '#10B981' }}>
                  Rs. {totalRevenue.toLocaleString()}
                </div>
                <span style={{ fontSize: '11px', color: '#10B981' }}>Total Received</span>
              </div>

              <div style={{ backgroundColor: '#1C2541', border: '1px solid #3A506B', borderRadius: '12px', padding: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', color: '#94A3B8', fontWeight: 600 }}>Pending Dues (بقایا)</span>
                  <AlertCircle size={18} color="#EF4444" />
                </div>
                <div style={{ fontSize: '24px', fontWeight: 800, marginTop: '8px', color: '#EF4444' }}>
                  Rs. {totalPending.toLocaleString()}
                </div>
                <span style={{ fontSize: '11px', color: '#EF4444' }}>Outstanding Balance</span>
              </div>
            </div>

            {/* Course-Wise Segregated Breakdown (CIT vs Trading) */}
            <div style={{ backgroundColor: '#1C2541', border: '1px solid #3A506B', borderRadius: '16px', padding: '20px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <div>
                  <h3 style={{ fontSize: '16px', fontWeight: 700, color: '#D4AF37' }}>
                    Course-wise Student & Fee Segregation
                  </h3>
                  <p style={{ fontSize: '12px', color: '#94A3B8' }}>
                    CIT والے الگ اور Trading والے الگ — تمام فیس اور بقایا جات کا مکمل ریکارڈ
                  </p>
                </div>
                <button
                  onClick={() => setActiveTab('students')}
                  style={{
                    backgroundColor: '#2D3A5D',
                    color: '#D4AF37',
                    border: '1px solid #3A506B',
                    borderRadius: '8px',
                    padding: '6px 12px',
                    fontSize: '12px',
                    fontWeight: 600
                  }}
                >
                  View All in Directory →
                </button>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '16px' }}>
                {courseSummaries.map(summary => {
                  const isCit = summary.courseName.toLowerCase() === 'cit';
                  const isTrading = summary.courseName.toLowerCase() === 'trading';
                  const borderColor = isCit ? '#38BDF8' : isTrading ? '#D4AF37' : '#3A506B';

                  return (
                    <div
                      key={summary.courseName}
                      style={{
                        backgroundColor: '#0B132B',
                        border: `1.5px solid ${borderColor}`,
                        borderRadius: '12px',
                        padding: '16px'
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          {isCit ? <Laptop size={18} color="#38BDF8" /> : isTrading ? <TrendingUp size={18} color="#D4AF37" /> : <GraduationCap size={18} color="#A855F7" />}
                          <span style={{ fontSize: '15px', fontWeight: 700, color: isCit ? '#38BDF8' : isTrading ? '#D4AF37' : '#F8F9FA' }}>
                            {summary.courseName} Course
                          </span>
                        </div>
                        <span style={{
                          backgroundColor: '#2D3A5D',
                          color: '#F8F9FA',
                          fontSize: '11px',
                          fontWeight: 700,
                          padding: '3px 8px',
                          borderRadius: '6px'
                        }}>
                          {summary.count} Students
                        </span>
                      </div>

                      <div style={{ marginTop: '12px', display: 'flex', justifyContent: 'space-between', fontSize: '12px', borderTop: '1px solid #1C2541', paddingTop: '8px' }}>
                        <div>
                          <span style={{ color: '#94A3B8', display: 'block', fontSize: '10px' }}>Total Fees</span>
                          <strong style={{ color: '#F8F9FA' }}>Rs. {summary.totalFee.toLocaleString()}</strong>
                        </div>
                        <div>
                          <span style={{ color: '#10B981', display: 'block', fontSize: '10px' }}>Paid (وصول)</span>
                          <strong style={{ color: '#10B981' }}>Rs. {summary.paidFee.toLocaleString()}</strong>
                        </div>
                        <div>
                          <span style={{ color: '#EF4444', display: 'block', fontSize: '10px' }}>Balance (بقایا)</span>
                          <strong style={{ color: summary.remainingFee > 0 ? '#EF4444' : '#10B981' }}>
                            Rs. {summary.remainingFee.toLocaleString()}
                          </strong>
                        </div>
                      </div>

                      <button
                        onClick={() => {
                          setSelectedCourseFilter(summary.courseName);
                          setActiveTab('students');
                        }}
                        style={{
                          width: '100%',
                          marginTop: '12px',
                          backgroundColor: '#2D3A5D',
                          color: '#F8F9FA',
                          border: 'none',
                          padding: '6px',
                          borderRadius: '6px',
                          fontSize: '11px',
                          fontWeight: 600,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          gap: '4px'
                        }}
                      >
                        <span>Manage {summary.courseName} Students</span>
                        <ChevronRight size={13} />
                      </button>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {/* ========================================================= */}
        {/* TAB 2: STUDENTS DIRECTORY (Grouped by Course) */}
        {/* ========================================================= */}
        {activeTab === 'students' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '12px' }}>
              <div>
                <h2 style={{ fontSize: '20px', fontWeight: 800, color: '#D4AF37' }}>
                  Student Directory & Admission Records
                </h2>
                <p style={{ fontSize: '12px', color: '#94A3B8' }}>
                  ہر کورس کے طلباء الگ الگ گروپ میں مع فیس اور بقایا جات
                </p>
              </div>

              <button
                onClick={() => setShowAddModal(true)}
                style={{
                  backgroundColor: '#D4AF37',
                  color: '#0B132B',
                  border: 'none',
                  padding: '8px 16px',
                  borderRadius: '8px',
                  fontWeight: 700,
                  fontSize: '13px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}
              >
                <Plus size={16} />
                <span>+ Admit Student</span>
              </button>
            </div>

            {/* Filter Bar */}
            <div style={{ display: 'flex', gap: '10px', marginBottom: '16px', flexWrap: 'wrap', alignItems: 'center' }}>
              <div style={{ flex: 1, minWidth: '220px', position: 'relative' }}>
                <Search size={16} color="#94A3B8" style={{ position: 'absolute', left: '12px', top: '10px' }} />
                <input
                  type="text"
                  placeholder="Search by name, roll no, mobile, course..."
                  value={searchQuery}
                  onChange={e => setSearchQuery(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '8px 12px 8px 36px',
                    borderRadius: '8px',
                    backgroundColor: '#1C2541',
                    border: '1px solid #3A506B',
                    color: '#F8F9FA',
                    fontSize: '13px'
                  }}
                />
              </div>

              {/* Course Chips */}
              <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                <button
                  onClick={() => setSelectedCourseFilter(null)}
                  style={{
                    padding: '6px 12px',
                    borderRadius: '8px',
                    fontSize: '12px',
                    fontWeight: !selectedCourseFilter ? 700 : 500,
                    backgroundColor: !selectedCourseFilter ? '#D4AF37' : '#1C2541',
                    color: !selectedCourseFilter ? '#0B132B' : '#F8F9FA',
                    border: '1px solid #3A506B'
                  }}
                >
                  All Courses ({students.length})
                </button>
                {courses.map(c => {
                  const isSel = selectedCourseFilter === c.name;
                  const count = students.filter(s => s.courseName === c.name).length;
                  return (
                    <button
                      key={c.id}
                      onClick={() => setSelectedCourseFilter(isSel ? null : c.name)}
                      style={{
                        padding: '6px 12px',
                        borderRadius: '8px',
                        fontSize: '12px',
                        fontWeight: isSel ? 700 : 500,
                        backgroundColor: isSel ? '#D4AF37' : '#1C2541',
                        color: isSel ? '#0B132B' : '#F8F9FA',
                        border: '1px solid #3A506B'
                      }}
                    >
                      {c.name} ({count})
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Grouped Courses Student List */}
            {Object.keys(groupedStudents).length === 0 ? (
              <div style={{ backgroundColor: '#1C2541', borderRadius: '12px', padding: '32px', textAlign: 'center', color: '#94A3B8' }}>
                <p>No student records found matching this criteria.</p>
              </div>
            ) : (
              Object.entries(groupedStudents).map(([courseName, studentList]) => {
                const isCit = courseName.toLowerCase() === 'cit';
                const isTrading = courseName.toLowerCase() === 'trading';
                const accentColor = isCit ? '#38BDF8' : isTrading ? '#D4AF37' : '#A855F7';
                const isCollapsed = !!collapsedCourses[courseName];

                const coursePaid = studentList.reduce((acc, s) => acc + s.paidFee, 0);
                const courseBalance = studentList.reduce((acc, s) => acc + s.remainingFee, 0);

                return (
                  <div key={courseName} style={{ marginBottom: '24px' }}>
                    {/* Course Group Header */}
                    <div style={{
                      backgroundColor: '#1C2541',
                      border: `1.5px solid ${accentColor}`,
                      borderRadius: '12px',
                      padding: '12px 16px',
                      marginBottom: '10px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      flexWrap: 'wrap',
                      gap: '8px'
                    }}>
                      <div 
                        onClick={() => toggleCourseCollapse(courseName)} 
                        style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' }}
                      >
                        <div style={{
                          width: '32px',
                          height: '32px',
                          borderRadius: '50%',
                          backgroundColor: `${accentColor}22`,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center'
                        }}>
                          {isCit ? <Laptop size={16} color={accentColor} /> : isTrading ? <TrendingUp size={16} color={accentColor} /> : <GraduationCap size={16} color={accentColor} />}
                        </div>
                        <div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <h3 style={{ fontSize: '15px', fontWeight: 800, color: accentColor }}>
                              {courseName} Course
                            </h3>
                            <span style={{ fontSize: '11px', fontWeight: 700, backgroundColor: '#2D3A5D', color: '#F8F9FA', padding: '2px 8px', borderRadius: '4px' }}>
                              {studentList.length} Students
                            </span>
                          </div>
                          <span style={{ fontSize: '11px', color: '#94A3B8' }}>
                            {isCit ? 'سی آئی ٹی - داخلہ اور فیس بریک ڈاؤن' : isTrading ? 'ٹریڈنگ - داخلہ اور فیس بریک ڈاؤن' : 'کورس طلباء و فیس ریکارڈ'}
                          </span>
                        </div>
                      </div>

                      {/* Header Financial Totals & Collapse Toggle */}
                      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                        <div style={{ fontSize: '12px', textAlign: 'right' }}>
                          <span style={{ color: '#10B981', fontWeight: 700, marginRight: '12px' }}>
                            Paid: Rs. {coursePaid.toLocaleString()}
                          </span>
                          <span style={{ color: courseBalance > 0 ? '#EF4444' : '#10B981', fontWeight: 700 }}>
                            Bal: Rs. {courseBalance.toLocaleString()}
                          </span>
                        </div>
                        <button
                          onClick={() => toggleCourseCollapse(courseName)}
                          style={{
                            background: 'transparent',
                            border: 'none',
                            color: accentColor,
                            display: 'flex',
                            alignItems: 'center'
                          }}
                        >
                          {isCollapsed ? <ChevronDown size={20} /> : <ChevronUp size={20} />}
                        </button>
                      </div>
                    </div>

                    {/* Students in this course */}
                    {!isCollapsed && (
                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '10px' }}>
                        {studentList.map(st => (
                          <div
                            key={st.id}
                            style={{
                              backgroundColor: '#1C2541',
                              border: '1px solid #3A506B',
                              borderRadius: '10px',
                              padding: '14px',
                              display: 'flex',
                              flexDirection: 'column',
                              justifyContent: 'space-between',
                              gap: '10px'
                            }}
                          >
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                              <div>
                                <h4 style={{ fontSize: '15px', fontWeight: 700, color: '#F8F9FA' }}>
                                  {st.name}
                                </h4>
                                <p style={{ fontSize: '11px', color: '#94A3B8' }}>
                                  S/D of {st.fatherName} • {st.rollNo}
                                </p>
                                <p style={{ fontSize: '11px', color: '#D4AF37', display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
                                  <Phone size={10} /> {st.phone}
                                </p>
                              </div>
                              <span style={{
                                backgroundColor: isCit ? '#38BDF822' : isTrading ? '#D4AF3722' : '#2D3A5D',
                                color: isCit ? '#38BDF8' : isTrading ? '#D4AF37' : '#F8F9FA',
                                border: `1px solid ${isCit ? '#38BDF8' : isTrading ? '#D4AF37' : '#3A506B'}`,
                                fontSize: '10px',
                                fontWeight: 700,
                                padding: '3px 8px',
                                borderRadius: '6px'
                              }}>
                                {st.courseName}
                              </span>
                            </div>

                            {/* Fee Card row */}
                            <div style={{
                              backgroundColor: '#0B132B',
                              borderRadius: '8px',
                              padding: '8px 12px',
                              display: 'flex',
                              justifyContent: 'space-between',
                              fontSize: '11px'
                            }}>
                              <div>
                                <span style={{ color: '#94A3B8', display: 'block', fontSize: '9px' }}>Total Fee</span>
                                <strong style={{ color: '#F8F9FA' }}>Rs. {st.totalFee.toLocaleString()}</strong>
                              </div>
                              <div>
                                <span style={{ color: '#10B981', display: 'block', fontSize: '9px' }}>Paid (وصول)</span>
                                <strong style={{ color: '#10B981' }}>Rs. {st.paidFee.toLocaleString()}</strong>
                              </div>
                              <div>
                                <span style={{ color: st.remainingFee > 0 ? '#EF4444' : '#10B981', display: 'block', fontSize: '9px' }}>Remaining (بقایا)</span>
                                <strong style={{ color: st.remainingFee > 0 ? '#EF4444' : '#10B981' }}>
                                  Rs. {st.remainingFee.toLocaleString()}
                                </strong>
                              </div>
                            </div>

                            {/* Collect Fee Button */}
                            {st.remainingFee > 0 ? (
                              <button
                                onClick={() => {
                                  setSelectedStudentForFee(st);
                                  setFeeAmountToPay(st.remainingFee.toString());
                                }}
                                style={{
                                  backgroundColor: '#10B981',
                                  color: '#0B132B',
                                  border: 'none',
                                  padding: '6px',
                                  borderRadius: '6px',
                                  fontSize: '11px',
                                  fontWeight: 700
                                }}
                              >
                                Collect Fee (بقایا وصول کریں)
                              </button>
                            ) : (
                              <div style={{ textAlign: 'center', fontSize: '11px', color: '#10B981', fontWeight: 700 }}>
                                ✓ Fee Fully Cleared
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                );
              })
            )}
          </div>
        )}

        {/* ========================================================= */}
        {/* TAB 3: FEE MANAGEMENT */}
        {/* ========================================================= */}
        {activeTab === 'fees' && (
          <div>
            <div style={{ marginBottom: '16px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 800, color: '#D4AF37' }}>
                Course-wise Fee Records & Balance Dues
              </h2>
              <p style={{ fontSize: '12px', color: '#94A3B8' }}>
                تمام کورسز کا فیس ریکارڈ، وصول شدہ رقم اور بقایا جات الگ الگ
              </p>
            </div>

            {/* Course Financial Cards */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '16px', marginBottom: '24px' }}>
              {courseSummaries.map(summary => (
                <div
                  key={summary.courseName}
                  style={{
                    backgroundColor: '#1C2541',
                    border: '1px solid #3A506B',
                    borderRadius: '12px',
                    padding: '16px'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3 style={{ fontSize: '16px', fontWeight: 700, color: '#D4AF37' }}>
                      {summary.courseName} Course
                    </h3>
                    <span style={{ fontSize: '12px', color: '#94A3B8' }}>{summary.count} Admissions</span>
                  </div>

                  <div style={{ marginTop: '12px', display: 'flex', justifyContent: 'space-between', backgroundColor: '#0B132B', padding: '10px', borderRadius: '8px' }}>
                    <div>
                      <span style={{ fontSize: '10px', color: '#94A3B8' }}>Total Fee</span>
                      <div style={{ fontSize: '13px', fontWeight: 700, color: '#F8F9FA' }}>Rs. {summary.totalFee.toLocaleString()}</div>
                    </div>
                    <div>
                      <span style={{ fontSize: '10px', color: '#10B981' }}>Paid (وصول)</span>
                      <div style={{ fontSize: '13px', fontWeight: 700, color: '#10B981' }}>Rs. {summary.paidFee.toLocaleString()}</div>
                    </div>
                    <div>
                      <span style={{ fontSize: '10px', color: '#EF4444' }}>Balance (بقایا)</span>
                      <div style={{ fontSize: '13px', fontWeight: 700, color: summary.remainingFee > 0 ? '#EF4444' : '#10B981' }}>
                        Rs. {summary.remainingFee.toLocaleString()}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Defaulter / Pending Balance Students */}
            <div style={{ backgroundColor: '#1C2541', borderRadius: '14px', border: '1px solid #3A506B', padding: '16px' }}>
              <h3 style={{ fontSize: '15px', fontWeight: 700, color: '#EF4444', marginBottom: '12px' }}>
                Students with Pending Fee Dues (بقایا والے طلباء)
              </h3>
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '12px' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid #3A506B', color: '#94A3B8', textAlign: 'left' }}>
                      <th style={{ padding: '8px' }}>Roll No</th>
                      <th style={{ padding: '8px' }}>Student Name</th>
                      <th style={{ padding: '8px' }}>Course</th>
                      <th style={{ padding: '8px' }}>Total Fee</th>
                      <th style={{ padding: '8px' }}>Paid</th>
                      <th style={{ padding: '8px' }}>Remaining Balance</th>
                      <th style={{ padding: '8px' }}>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {students.filter(s => s.remainingFee > 0).map(st => (
                      <tr key={st.id} style={{ borderBottom: '1px solid #2D3A5D' }}>
                        <td style={{ padding: '8px', color: '#D4AF37', fontWeight: 600 }}>{st.rollNo}</td>
                        <td style={{ padding: '8px', fontWeight: 600 }}>{st.name}</td>
                        <td style={{ padding: '8px' }}>{st.courseName}</td>
                        <td style={{ padding: '8px' }}>Rs. {st.totalFee.toLocaleString()}</td>
                        <td style={{ padding: '8px', color: '#10B981', fontWeight: 600 }}>Rs. {st.paidFee.toLocaleString()}</td>
                        <td style={{ padding: '8px', color: '#EF4444', fontWeight: 700 }}>Rs. {st.remainingFee.toLocaleString()}</td>
                        <td style={{ padding: '8px' }}>
                          <button
                            onClick={() => {
                              setSelectedStudentForFee(st);
                              setFeeAmountToPay(st.remainingFee.toString());
                            }}
                            style={{
                              backgroundColor: '#10B981',
                              color: '#0B132B',
                              border: 'none',
                              padding: '4px 8px',
                              borderRadius: '4px',
                              fontWeight: 700,
                              fontSize: '11px'
                            }}
                          >
                            Collect
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================= */}
        {/* TAB 4: COURSES DIRECTORY */}
        {/* ========================================================= */}
        {activeTab === 'courses' && (
          <div>
            <div style={{ marginBottom: '16px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 800, color: '#D4AF37' }}>
                Course Offerings & Curriculums
              </h2>
              <p style={{ fontSize: '12px', color: '#94A3B8' }}>
                CIT, Trading, Spoken English, Graphic Designing, DIT, Digital Marketing
              </p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '16px' }}>
              {courses.map(course => (
                <div
                  key={course.id}
                  style={{
                    backgroundColor: '#1C2541',
                    border: '1px solid #3A506B',
                    borderRadius: '12px',
                    padding: '16px',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between'
                  }}
                >
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span style={{ fontSize: '11px', color: '#D4AF37', fontWeight: 700 }}>{course.category}</span>
                      <span style={{ fontSize: '11px', backgroundColor: '#2D3A5D', padding: '2px 8px', borderRadius: '4px' }}>{course.duration}</span>
                    </div>
                    <h3 style={{ fontSize: '17px', fontWeight: 800, color: '#F8F9FA', marginTop: '6px' }}>{course.name}</h3>
                    <p style={{ fontSize: '12px', color: '#94A3B8', marginTop: '4px' }}>Instructor: {course.instructor}</p>
                  </div>

                  <div style={{ marginTop: '16px', borderTop: '1px solid #2D3A5D', paddingTop: '12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <span style={{ fontSize: '10px', color: '#94A3B8' }}>Standard Fee</span>
                      <div style={{ fontSize: '15px', fontWeight: 800, color: '#10B981' }}>Rs. {course.totalFee.toLocaleString()}</div>
                    </div>
                    <button
                      onClick={() => {
                        setNewCourseName(course.name);
                        setShowAddModal(true);
                      }}
                      style={{
                        backgroundColor: '#D4AF37',
                        color: '#0B132B',
                        border: 'none',
                        padding: '6px 12px',
                        borderRadius: '6px',
                        fontWeight: 700,
                        fontSize: '11px'
                      }}
                    >
                      + Admit Student
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* ========================================================= */}
        {/* TAB 5: DOWNLOAD ANDROID APK */}
        {/* ========================================================= */}
        {activeTab === 'apk' && (
          <div style={{ maxWidth: '720px', margin: '0 auto', textAlign: 'center', backgroundColor: '#1C2541', border: '1.5px solid #D4AF37', borderRadius: '16px', padding: '32px 20px' }}>
            <div style={{
              width: '88px',
              height: '88px',
              borderRadius: '50%',
              backgroundColor: 'rgba(212, 175, 55, 0.2)',
              border: '2px solid #D4AF37',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 16px',
              overflow: 'hidden'
            }}>
              <img 
                src="/app-icon.png" 
                alt="Al Ghazi Digital Institute Emblem" 
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                onError={(e) => {
                  (e.target as HTMLElement).style.display = 'none';
                }}
              />
            </div>

            <h2 style={{ fontSize: '22px', fontWeight: 800, color: '#F8F9FA' }}>
              Download Al Ghazi Digital Institute APK
            </h2>
            <p style={{ color: '#94A3B8', fontSize: '13px', marginTop: '8px', maxWidth: '520px', margin: '8px auto 20px' }}>
              Install the official Android application with custom launcher icon, offline attendance, thermal receipt printing, SMS dispatch, and biometric protection.
            </p>

            <div style={{
              backgroundColor: '#0B132B',
              borderRadius: '12px',
              padding: '16px',
              display: 'inline-flex',
              gap: '24px',
              marginBottom: '24px',
              textAlign: 'left'
            }}>
              <div>
                <span style={{ fontSize: '10px', color: '#94A3B8', display: 'block' }}>Package File</span>
                <strong style={{ fontSize: '12px', color: '#F8F9FA' }}>app-debug.apk</strong>
              </div>
              <div>
                <span style={{ fontSize: '10px', color: '#94A3B8', display: 'block' }}>Size</span>
                <strong style={{ fontSize: '12px', color: '#10B981' }}>28.65 MB</strong>
              </div>
              <div>
                <span style={{ fontSize: '10px', color: '#94A3B8', display: 'block' }}>Integrity</span>
                <strong style={{ fontSize: '12px', color: '#D4AF37' }}>Verified Android APK</strong>
              </div>
            </div>

            <br />

            <div style={{ display: 'flex', justifyContent: 'center', gap: '12px', flexWrap: 'wrap' }}>
              <a
                href="/app-debug.apk"
                download="Al-Ghazi-Digital-Institute.apk"
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '8px',
                  backgroundColor: '#10B981',
                  color: '#0B132B',
                  textDecoration: 'none',
                  padding: '12px 28px',
                  borderRadius: '10px',
                  fontSize: '15px',
                  fontWeight: 800,
                  boxShadow: '0 4px 14px rgba(16, 185, 129, 0.4)'
                }}
              >
                <Download size={18} />
                <span>Direct Download APK (28.65 MB)</span>
              </a>

              <a
                href="https://github.com/niazq360-web/Al-Ghazi-Digital-Institute/raw/main/APK_DOWNLOAD/app-debug.apk"
                download="Al-Ghazi-Digital-Institute.apk"
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '8px',
                  backgroundColor: '#2D3A5D',
                  color: '#F8F9FA',
                  textDecoration: 'none',
                  padding: '12px 20px',
                  borderRadius: '10px',
                  fontSize: '14px',
                  fontWeight: 600,
                  border: '1px solid #3A506B'
                }}
              >
                <Download size={16} />
                <span>GitHub Mirror</span>
              </a>
            </div>

            <div style={{ marginTop: '24px', fontSize: '11px', color: '#94A3B8' }}>
              <ShieldCheck size={14} color="#D4AF37" style={{ display: 'inline', verticalAlign: 'middle', marginRight: '4px' }} />
              Direct installable package built with Gradle. Android 7.0 (API 24) and above compatible.
            </div>
          </div>
        )}

      </main>

      {/* ========================================================= */}
      {/* MODAL: NEW ADMISSION */}
      {/* ========================================================= */}
      {showAddModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.7)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 100,
          padding: '16px'
        }}>
          <div style={{
            backgroundColor: '#1C2541',
            border: '1px solid #D4AF37',
            borderRadius: '16px',
            padding: '24px',
            maxWidth: '440px',
            width: '100%'
          }}>
            <h3 style={{ fontSize: '18px', fontWeight: 800, color: '#D4AF37', marginBottom: '14px' }}>
              Admit New Student (نیا طالب علم)
            </h3>

            <form onSubmit={handleAddStudent} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Full Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Muhammad Ali"
                  value={newStudentName}
                  onChange={e => setNewStudentName(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Father's Name</label>
                <input
                  type="text"
                  placeholder="e.g. Ghulam Qadir"
                  value={newFatherName}
                  onChange={e => setNewFatherName(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Mobile Number *</label>
                <input
                  type="text"
                  required
                  placeholder="03001234567"
                  value={newPhone}
                  onChange={e => setNewPhone(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Select Course (CIT / Trading / etc.)</label>
                <select
                  value={newCourseName}
                  onChange={e => setNewCourseName(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                >
                  {courses.map(c => (
                    <option key={c.id} value={c.name}>{c.name} — Rs. {c.totalFee.toLocaleString()}</option>
                  ))}
                </select>
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Initial Paid Fee at Admission (Rs.)</label>
                <input
                  type="number"
                  placeholder="0"
                  value={newInitialPaid}
                  onChange={e => setNewInitialPaid(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '12px' }}>
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  style={{ padding: '8px 14px', borderRadius: '6px', backgroundColor: '#2D3A5D', border: 'none', color: '#F8F9FA', fontSize: '12px' }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  style={{ padding: '8px 16px', borderRadius: '6px', backgroundColor: '#D4AF37', border: 'none', color: '#0B132B', fontWeight: 700, fontSize: '12px' }}
                >
                  Complete Admission
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================= */}
      {/* MODAL: COLLECT FEE WITH MONTH SELECTION */}
      {/* ========================================================= */}
      {selectedStudentForFee && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.75)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 100,
          padding: '16px'
        }}>
          <div style={{
            backgroundColor: '#1C2541',
            border: '1.5px solid #10B981',
            borderRadius: '16px',
            padding: '24px',
            maxWidth: '460px',
            width: '100%',
            boxShadow: '0 8px 32px rgba(0,0,0,0.5)'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 800, color: '#10B981', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Receipt size={20} />
                <span>Fee Collection & Receipt (فیس وصولی)</span>
              </h3>
              <span style={{ fontSize: '11px', backgroundColor: '#0B132B', color: '#D4AF37', padding: '3px 8px', borderRadius: '4px', fontWeight: 700 }}>
                {selectedStudentForFee.courseName}
              </span>
            </div>

            <p style={{ fontSize: '12px', color: '#94A3B8', marginBottom: '16px', borderBottom: '1px solid #3A506B', pb: '8px' }}>
              Student: <strong style={{ color: '#F8F9FA' }}>{selectedStudentForFee.name}</strong> ({selectedStudentForFee.rollNo})<br />
              Total Course Fee: <strong>Rs. {selectedStudentForFee.totalFee.toLocaleString()}</strong> • 
              Due Balance: <strong style={{ color: '#EF4444' }}>Rs. {selectedStudentForFee.remainingFee.toLocaleString()}</strong>
            </p>

            <form onSubmit={handlePayFee} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {/* Fee Month Selection (Current vs Previous Months) */}
              <div>
                <label style={{ fontSize: '12px', color: '#D4AF37', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '6px' }}>
                  <Calendar size={14} />
                  <span>Fee For Month / فیس برائے ماہ:</span>
                </label>
                
                {/* Month Quick Chips */}
                <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap', marginBottom: '8px' }}>
                  {[
                    { label: 'Current: Oct 2026', value: 'October 2026', isPrev: false },
                    { label: 'Prev: Sep 2026 (پچھلا)', value: 'September 2026', isPrev: true },
                    { label: 'Aug 2026', value: 'August 2026', isPrev: true },
                    { label: 'Jul 2026', value: 'July 2026', isPrev: true }
                  ].map(m => {
                    const isSelected = selectedFeeMonth === m.value;
                    return (
                      <button
                        type="button"
                        key={m.value}
                        onClick={() => setSelectedFeeMonth(m.value)}
                        style={{
                          padding: '5px 10px',
                          borderRadius: '6px',
                          fontSize: '11px',
                          fontWeight: isSelected ? 700 : 500,
                          backgroundColor: isSelected ? (m.isPrev ? '#F59E0B' : '#10B981') : '#0B132B',
                          color: isSelected ? '#0B132B' : '#F8F9FA',
                          border: isSelected ? '1px solid #FFF' : '1px solid #3A506B',
                          cursor: 'pointer'
                        }}
                      >
                        {m.label}
                      </button>
                    );
                  })}
                </div>

                {/* Notice if Previous Month is Selected */}
                {selectedFeeMonth !== 'October 2026' && (
                  <div style={{
                    backgroundColor: 'rgba(245, 158, 11, 0.15)',
                    border: '1px solid #F59E0B',
                    borderRadius: '8px',
                    padding: '8px 12px',
                    fontSize: '11px',
                    color: '#FBBF24',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px'
                  }}>
                    <History size={15} />
                    <span>Paying for <strong>{selectedFeeMonth} (پچھلا مہینہ)</strong>. رسيد پر پچھلا مہینہ واضح درج ہوگا۔</span>
                  </div>
                )}
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Amount to Pay (Rs.) *</label>
                <input
                  type="number"
                  required
                  min="1"
                  max={selectedStudentForFee.remainingFee}
                  value={feeAmountToPay}
                  onChange={e => setFeeAmountToPay(e.target.value)}
                  style={{ width: '100%', padding: '10px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '15px', fontWeight: 700 }}
                />
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Payment Method (طریقہ ادائیگی)</label>
                <select
                  value={paymentMethod}
                  onChange={e => setPaymentMethod(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                >
                  <option value="Cash">Cash (نقد)</option>
                  <option value="EasyPaisa">EasyPaisa</option>
                  <option value="JazzCash">JazzCash</option>
                  <option value="Bank Transfer">Bank Transfer / Online</option>
                </select>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '12px' }}>
                <button
                  type="button"
                  onClick={() => setSelectedStudentForFee(null)}
                  style={{ padding: '8px 14px', borderRadius: '6px', backgroundColor: '#2D3A5D', border: 'none', color: '#F8F9FA', fontSize: '12px', cursor: 'pointer' }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  style={{ padding: '8px 20px', borderRadius: '6px', backgroundColor: '#10B981', border: 'none', color: '#0B132B', fontWeight: 800, fontSize: '13px', cursor: 'pointer' }}
                >
                  Confirm Payment & Generate Receipt →
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================= */}
      {/* MODAL: OFFICIAL PRINTABLE RECEIPT */}
      {/* ========================================================= */}
      {generatedReceipt && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.85)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 110,
          padding: '16px'
        }}>
          <div style={{
            backgroundColor: '#FFFFFF',
            color: '#0A192F',
            borderRadius: '16px',
            padding: '28px',
            maxWidth: '480px',
            width: '100%',
            boxShadow: '0 12px 40px rgba(0,0,0,0.6)',
            border: '2px solid #D4AF37',
            fontFamily: 'system-ui, sans-serif'
          }}>
            {/* Header */}
            <div style={{ textAlign: 'center', borderBottom: '2px dashed #D4AF37', paddingBottom: '16px', marginBottom: '16px' }}>
              <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: '48px', height: '48px', borderRadius: '50%', backgroundColor: '#0A192F', color: '#D4AF37', marginBottom: '8px' }}>
                <GraduationCap size={28} />
              </div>
              <h2 style={{ fontSize: '20px', fontWeight: 900, color: '#0A192F', letterSpacing: '0.5px', margin: 0 }}>
                AL GHAZI DIGITAL INSTITUTE
              </h2>
              <p style={{ fontSize: '11px', color: '#475569', fontWeight: 600, margin: '2px 0 0 0' }}>
                Official Fee Payment Voucher • رسید برائے فیس
              </p>
              
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '12px', fontSize: '11px', color: '#64748B' }}>
                <span><strong>Receipt No:</strong> {generatedReceipt.receiptNo}</span>
                <span><strong>Date:</strong> {generatedReceipt.paymentDate}</span>
              </div>
            </div>

            {/* Fee Month Highlight Badge */}
            <div style={{
              backgroundColor: generatedReceipt.isPreviousMonth ? '#FEF3C7' : '#ECFDF5',
              border: `1.5px solid ${generatedReceipt.isPreviousMonth ? '#F59E0B' : '#10B981'}`,
              borderRadius: '8px',
              padding: '10px',
              textAlign: 'center',
              marginBottom: '16px'
            }}>
              <span style={{ fontSize: '11px', color: '#475569', display: 'block', fontWeight: 600 }}>FEE PAID FOR MONTH (فیس برائے ماہ)</span>
              <strong style={{ fontSize: '15px', color: generatedReceipt.isPreviousMonth ? '#B45309' : '#047857' }}>
                {generatedReceipt.feeMonth} {generatedReceipt.isPreviousMonth ? '(پچھلا مہینہ / Previous Month)' : ''}
              </strong>
            </div>

            {/* Student Details Table */}
            <div style={{ fontSize: '12px', display: 'flex', flexDirection: 'column', gap: '8px', borderBottom: '1px solid #E2E8F0', paddingBottom: '12px', marginBottom: '12px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748B' }}>Student Name:</span>
                <strong style={{ color: '#0F172A' }}>{generatedReceipt.studentName}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748B' }}>Father's Name:</span>
                <span style={{ color: '#0F172A' }}>{generatedReceipt.fatherName}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748B' }}>Roll No / ID:</span>
                <span style={{ color: '#0F172A', fontWeight: 700 }}>{generatedReceipt.rollNo}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748B' }}>Enrolled Course:</span>
                <strong style={{ color: '#0F172A' }}>{generatedReceipt.courseName}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#64748B' }}>Payment Method:</span>
                <span style={{ color: '#0F172A' }}>{generatedReceipt.paymentMethod}</span>
              </div>
            </div>

            {/* Financial Numbers */}
            <div style={{ fontSize: '13px', display: 'flex', flexDirection: 'column', gap: '8px', marginBottom: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748B' }}>
                <span>Total Course Fee:</span>
                <span>Rs. {generatedReceipt.totalFee.toLocaleString()}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748B' }}>
                <span>Previous Paid:</span>
                <span>Rs. {generatedReceipt.previousPaid.toLocaleString()}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', backgroundColor: '#F1F5F9', padding: '8px', borderRadius: '6px', fontSize: '15px' }}>
                <strong style={{ color: '#047857' }}>Amount Paid Now:</strong>
                <strong style={{ color: '#047857' }}>Rs. {generatedReceipt.amountPaid.toLocaleString()}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', color: generatedReceipt.newRemaining > 0 ? '#DC2626' : '#047857', fontWeight: 700 }}>
                <span>Remaining Balance Due:</span>
                <span>Rs. {generatedReceipt.newRemaining.toLocaleString()}</span>
              </div>
            </div>

            <p style={{ textAlign: 'center', fontSize: '10px', color: '#94A3B8', margin: '0 0 16px 0' }}>
              Computer generated official receipt. Thank you for choosing Al Ghazi Digital Institute.
            </p>

            {/* Action Buttons */}
            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                type="button"
                onClick={() => window.print()}
                style={{
                  flex: 1,
                  padding: '10px',
                  borderRadius: '8px',
                  backgroundColor: '#0A192F',
                  color: '#D4AF37',
                  border: 'none',
                  fontWeight: 700,
                  fontSize: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '6px',
                  cursor: 'pointer'
                }}
              >
                <Printer size={16} />
                <span>Print Receipt (پرنٹ)</span>
              </button>
              <button
                type="button"
                onClick={() => setGeneratedReceipt(null)}
                style={{
                  padding: '10px 18px',
                  borderRadius: '8px',
                  backgroundColor: '#E2E8F0',
                  color: '#0F172A',
                  border: 'none',
                  fontWeight: 600,
                  fontSize: '12px',
                  cursor: 'pointer'
                }}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ========================================================= */}
      {/* MODAL: ADD EXPENSE */}
      {/* ========================================================= */}
      {showExpenseModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.7)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 100,
          padding: '16px'
        }}>
          <div style={{
            backgroundColor: '#1C2541',
            border: '1px solid #EF4444',
            borderRadius: '16px',
            padding: '24px',
            maxWidth: '400px',
            width: '100%'
          }}>
            <h3 style={{ fontSize: '17px', fontWeight: 800, color: '#EF4444', marginBottom: '14px' }}>
              Record Operating Expense (نیا خرچہ)
            </h3>

            <form onSubmit={handleAddExpense} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Expense Title / Description *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Campus Generator Fuel"
                  value={newExpenseTitle}
                  onChange={e => setNewExpenseTitle(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Category</label>
                <select
                  value={newExpenseCategory}
                  onChange={e => setNewExpenseCategory(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                >
                  <option value="Rent">Campus Rent (کرایہ)</option>
                  <option value="Salaries">Instructor & Staff Salaries (تنخواہیں)</option>
                  <option value="Utilities">Electricity & Generator (بل و بجلی)</option>
                  <option value="Marketing">Marketing & Admission Ads (اشتہارات)</option>
                  <option value="Technology">Internet & Tech Equipment</option>
                  <option value="Miscellaneous">Miscellaneous (متفرق)</option>
                </select>
              </div>

              <div>
                <label style={{ fontSize: '11px', color: '#94A3B8', display: 'block', marginBottom: '4px' }}>Amount (Rs.) *</label>
                <input
                  type="number"
                  required
                  min="1"
                  placeholder="e.g. 5000"
                  value={newExpenseAmount}
                  onChange={e => setNewExpenseAmount(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', backgroundColor: '#0B132B', border: '1px solid #3A506B', color: '#F8F9FA', fontSize: '13px' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '10px' }}>
                <button
                  type="button"
                  onClick={() => setShowExpenseModal(false)}
                  style={{ padding: '8px 14px', borderRadius: '6px', backgroundColor: '#2D3A5D', border: 'none', color: '#F8F9FA', fontSize: '12px' }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  style={{ padding: '8px 16px', borderRadius: '6px', backgroundColor: '#EF4444', border: 'none', color: '#FFFFFF', fontWeight: 700, fontSize: '12px' }}
                >
                  Save Expense
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Footer */}
      <footer style={{ borderTop: '1px solid #3A506B', padding: '20px 16px', textAlign: 'center', fontSize: '11px', color: '#94A3B8', marginTop: '40px' }}>
        Al Ghazi Digital Institute • All Rights Reserved 2026 • Hosted on GitHub Pages via Automated GitHub Actions
      </footer>
    </div>
  );
}
