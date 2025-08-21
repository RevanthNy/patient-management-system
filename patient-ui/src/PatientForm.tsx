import { useState, useEffect } from 'react';
import axios from 'axios';
import { isEqual } from 'lodash';
import {
  TextField, Button, Grid, Box, Typography, Select, MenuItem,
  FormControl, InputLabel, IconButton, RadioGroup, FormControlLabel,
  Radio, FormLabel, Divider, Paper, useMediaQuery, useTheme, OutlinedInput
} from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';

const medicalConditions = [
  'None', 'Acne', 'Allergies', 'Alzheimer\'s Disease', 'Anxiety', 'Asthma',
  'Celiac Disease', 'Hypertension', 'Hypothyroidism',
  'Migraine', 'Arthritis'
];

const initialPatientState = {
  firstName: '', lastName: '', email: '', phoneNumber: '', heightCm: '',
  weightKg: '', dateOfBirth: '', ethnicity: 'White', typeOfDiabetes: 'Type 1',
  dateOfDiagnosis: '', biologicalSex: 'Male', notes: '',
  assignedPhysician: 'Jane Doe',
  medicalHistory: [],
  address: { country: 'USA', zipcode: '', mailingAddress: '', state: 'CA', county: 'Orange County' },
  caregivers: []
};

function PatientForm({ patient: editingPatient, onFormSubmit, onCancel }) {
  const [patient, setPatient] = useState(initialPatientState);
  const [initialFormState, setInitialFormState] = useState(initialPatientState);
  const [errors, setErrors] = useState({});
  const isEditMode = !!editingPatient;
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));

  useEffect(() => {
    if (isEditMode && editingPatient) {
      const history = typeof editingPatient.medicalHistory === 'string'
        ? editingPatient.medicalHistory.split(', ').filter(Boolean)
        : editingPatient.medicalHistory || [];

      const patientData = { ...editingPatient, medicalHistory: history, caregivers: editingPatient.caregivers || [] };
      setPatient(patientData);
      setInitialFormState(JSON.parse(JSON.stringify(patientData)));
    } else {
      setPatient(initialPatientState);
      setInitialFormState(initialPatientState);
    }
  }, [editingPatient, isEditMode]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.startsWith('address.')) {
      const addressField = name.split('.')[1];
      setPatient(prev => ({ ...prev, address: { ...prev.address, [addressField]: value } }));
    } else {
      setPatient(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleCaregiverChange = (index, e) => {
    const { name, value } = e.target;
    const updatedCaregivers = [...(patient.caregivers || [])];
    if (updatedCaregivers[index]) {
      updatedCaregivers[index] = { ...updatedCaregivers[index], [name]: value };
      setPatient(prev => ({ ...prev, caregivers: updatedCaregivers }));
    }
  };

  const handleAddCaregiver = () => {
    setPatient(prev => ({ ...prev, caregivers: [...(prev.caregivers || []), { firstName: '', lastName: '', email: '', phoneNumber: '', relationshipToPatient: 'Family Member' }] }));
  };

  const handleDeleteCaregiver = (index) => {
    const updatedCaregivers = patient.caregivers?.filter((_, i) => i !== index) || [];
    setPatient(prev => ({ ...prev, caregivers: updatedCaregivers }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});

    if (isEditMode && isEqual(initialFormState, patient)) {
      alert('No changes detected.');
      return;
    }

    const payload = { ...patient, medicalHistory: Array.isArray(patient.medicalHistory) ? patient.medicalHistory : [] };

    try {
      if (isEditMode) {
        await axios.put(`http://localhost:8080/api/patients/${editingPatient.id}`, payload);
        onFormSubmit({ type: 'success', text: `Patient "${patient.firstName} ${patient.lastName}" updated successfully.` });
      } else {
        await axios.post('http://localhost:8080/api/patients', payload);
        onFormSubmit({ type: 'success', text: `Patient "${patient.firstName} ${patient.lastName}" created successfully.` });
      }
    } catch (error) {
      if (error.response) {
        if (typeof error.response.data === 'string') {
          onFormSubmit({ type: 'error', text: error.response.data });
        } else if (typeof error.response.data === 'object' && error.response.data !== null) {
          setErrors(error.response.data);
          onFormSubmit({ type: 'error', text: 'A record already exists with same details' });
        } else {
          onFormSubmit({ type: 'error', text: 'An unexpected error occurred.' });
        }
      } else {
        onFormSubmit({ type: 'error', text: 'A network error occurred. Please check your connection.' });
        console.error("Submission failed:", error);
      }
    }
  };
  return (
    <Paper elevation={0} sx={{ p: isSmallScreen ? 1 : 2 }}>
      <Typography variant="h5" gutterBottom align="center" color="primary">{isEditMode ? 'Edit Patient Details' : 'Add Patient Details'}</Typography>
      <form onSubmit={handleSubmit}>
        <Box sx={{ p: isSmallScreen ? 1 : 2 }}>
          <Grid container spacing={isSmallScreen ? 2 : 3}>
            <Grid item xs={12} sm={6} md={3}><TextField fullWidth label="First Name" name="firstName" value={patient.firstName} onChange={handleChange} required error={!!errors.firstName} helperText={errors.firstName} /></Grid>
            <Grid item xs={12} sm={6} md={3}><TextField fullWidth label="Last Name" name="lastName" value={patient.lastName} onChange={handleChange} required error={!!errors.lastName} helperText={errors.lastName} /></Grid>
            <Grid item xs={12} sm={6} md={3}><TextField fullWidth type="email" label="Email Address" name="email" value={patient.email} onChange={handleChange} required error={!!errors.email} helperText={errors.email} /></Grid>
            <Grid item xs={12} sm={6} md={3}><TextField fullWidth label="Phone Number" name="phoneNumber" value={patient.phoneNumber} onChange={handleChange} required error={!!errors.phoneNumber} helperText={errors.phoneNumber} /></Grid>

            <Grid item xs={12} sm={6} md={3}><TextField fullWidth type="number" label="Height (cm)" name="heightCm" value={patient.heightCm} onChange={handleChange} required error={!!errors.heightCm} helperText={errors.heightCm} /></Grid>
            <Grid item xs={12} sm={6} md={3}><TextField fullWidth type="number" label="Weight (kg)" name="weightKg" value={patient.weightKg} onChange={handleChange} required error={!!errors.weightKg} helperText={errors.weightKg} /></Grid>
            <Grid item xs={12} sm={6} md={3}><TextField fullWidth label="DOB" type="date" name="dateOfBirth" value={patient.dateOfBirth} onChange={handleChange} InputLabelProps={{ shrink: true }} required error={!!errors.dateOfBirth} helperText={errors.dateOfBirth} /></Grid>
            <Grid item xs={12} sm={6} md={3}><FormControl fullWidth required error={!!errors.ethnicity}><InputLabel>Ethnicity</InputLabel><Select name="ethnicity" value={patient.ethnicity} label="Ethnicity" onChange={handleChange}><MenuItem value="White">White</MenuItem><MenuItem value="Hispanic">Hispanic or Latino</MenuItem><MenuItem value="Black">Black or African American</MenuItem><MenuItem value="Asian">Asian</MenuItem><MenuItem value="Other">Other</MenuItem></Select></FormControl></Grid>

            <Grid item xs={12} sm={6} md={3}><FormControl fullWidth required error={!!errors.typeOfDiabetes}><InputLabel>Type of Diabetes</InputLabel><Select name="typeOfDiabetes" value={patient.typeOfDiabetes} label="Type of Diabetes" onChange={handleChange}><MenuItem value="Not Applicable">Not Applicable</MenuItem><MenuItem value="Type 1">Type 1</MenuItem><MenuItem value="Type 2">Type 2</MenuItem><MenuItem value="Gestational">Gestational</MenuItem></Select></FormControl></Grid>
            <Grid item xs={12} sm={6} md={3}><TextField fullWidth label="Date of Diagnosis" type="date" name="dateOfDiagnosis" value={patient.dateOfDiagnosis} onChange={handleChange} InputLabelProps={{ shrink: true }} required error={!!errors.dateOfDiagnosis} helperText={errors.dateOfDiagnosis} /></Grid>
            <Grid item xs={12} sm={6} md={6}><FormControl component="fieldset"><FormLabel component="legend">Biological Sex</FormLabel><RadioGroup row name="biologicalSex" value={patient.biologicalSex} onChange={handleChange}><FormControlLabel value="Male" control={<Radio />} label="Male" /><FormControlLabel value="Female" control={<Radio />} label="Female" /><FormControlLabel value="Intersex" control={<Radio />} label="Intersex" /></RadioGroup></FormControl></Grid>

            <Grid item xs={12} sm={6} md={4}><FormControl fullWidth required><InputLabel>Assign to a Physician/Group</InputLabel><Select name="assignedPhysician" value={patient.assignedPhysician} label="Assign to a Physician/Group" onChange={handleChange}><MenuItem value="Jane Doe">Dr. Jane Doe</MenuItem><MenuItem value="John Smith">Dr. John Smith</MenuItem><MenuItem value="Emily Carter">Dr. Emily Carter</MenuItem></Select></FormControl></Grid>
            <Grid item xs={12} sm={12} md={8}><FormControl fullWidth><InputLabel id="medical-history-label">Medical History</InputLabel><Select labelId="medical-history-label" multiple name="medicalHistory" value={patient.medicalHistory} onChange={handleChange} input={<OutlinedInput label="Medical History" />} renderValue={(selected) => selected.join(', ')}>{medicalConditions.map((c) => (<MenuItem key={c} value={c}>{c}</MenuItem>))}</Select></FormControl></Grid>

            <Grid item xs={12}><TextField fullWidth multiline rows={2} label="Notes" name="notes" value={patient.notes} onChange={handleChange} /></Grid>

            <Grid item xs={12} sm={6} md={4}><FormControl fullWidth required><InputLabel>Country</InputLabel><Select name="address.country" value={patient.address.country} label="Country" onChange={handleChange}><MenuItem value="USA">USA</MenuItem><MenuItem value="Canada">Canada</MenuItem></Select></FormControl></Grid>
            <Grid item xs={12} sm={6} md={4}><TextField fullWidth label="Zipcode" name="address.zipcode" value={patient.address.zipcode} onChange={handleChange} required /></Grid>
            <Grid item xs={12} md={4}><TextField fullWidth label="Mailing Address" name="address.mailingAddress" value={patient.address.mailingAddress} onChange={handleChange} required /></Grid>
            <Grid item xs={12} sm={6} md={4}><FormControl fullWidth required><InputLabel>State</InputLabel><Select name="address.state" value={patient.address.state} label="State" onChange={handleChange}><MenuItem value="CA">California</MenuItem><MenuItem value="TX">Texas</MenuItem><MenuItem value="FL">Florida</MenuItem></Select></FormControl></Grid>
            <Grid item xs={12} sm={6} md={4}><FormControl fullWidth required><InputLabel>County</InputLabel><Select name="address.county" value={patient.address.county} label="County" onChange={handleChange}><MenuItem value="Orange County">Orange County</MenuItem><MenuItem value="Los Angeles County">Los Angeles County</MenuItem></Select></FormControl></Grid>
          </Grid>
        </Box>
        <Divider sx={{ my: 3 }}><Box display="flex" alignItems="center"><IconButton color="primary" onClick={handleAddCaregiver}><AddCircleIcon /></IconButton><Typography variant="h6" sx={{ ml: 1 }}>Add Caregiver</Typography></Box></Divider>
        <Grid container spacing={3}>
          {patient.caregivers?.map((caregiver, index) => (
            <Grid item xs={12} key={index}><Paper elevation={2} sx={{ p: 2 }}><Box display="flex" justifyContent="space-between" alignItems="center" mb={2}><Typography variant="subtitle1" fontWeight="bold">Caregiver {index + 1}</Typography><Button size="small" color="error" onClick={() => handleDeleteCaregiver(index)}>Delete</Button></Box><Grid container spacing={2}><Grid item xs={12} sm={6}><TextField fullWidth label="First Name" name="firstName" value={caregiver.firstName} onChange={e => handleCaregiverChange(index, e)} required /></Grid><Grid item xs={12} sm={6}><TextField fullWidth label="Last Name" name="lastName" value={caregiver.lastName} onChange={e => handleCaregiverChange(index, e)} required /></Grid><Grid item xs={12}><TextField fullWidth type="email" label="Email Address" name="email" value={caregiver.email} onChange={e => handleCaregiverChange(index, e)} required /></Grid><Grid item xs={12}><TextField fullWidth label="Phone Number" name="phoneNumber" value={caregiver.phoneNumber} onChange={e => handleCaregiverChange(index, e)} required /></Grid><Grid item xs={12}><FormControl fullWidth required><InputLabel>Relationship to Patient</InputLabel><Select name="relationshipToPatient" value={caregiver.relationshipToPatient} label="Relationship to Patient" onChange={e => handleCaregiverChange(index, e)}><MenuItem value="Spouse">Spouse</MenuItem><MenuItem value="Parent">Parent</MenuItem><MenuItem value="Child">Child</MenuItem><MenuItem value="Sibling">Sibling</MenuItem><MenuItem value="Friend">Friend</MenuItem></Select></FormControl></Grid></Grid></Paper></Grid>
          ))}
        </Grid>
        <Box sx={{ mt: 4, display: 'flex', justifyContent: 'center', gap: 2 }}>
          <Button type="submit" variant="contained" color="primary" size="large">{isEditMode ? 'Update Patient' : 'Submit Patient'}</Button>
          <Button variant="outlined" size="large" onClick={onCancel}>Cancel</Button>
        </Box>
      </form>
    </Paper>
  );
}

export default PatientForm;
