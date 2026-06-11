import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Switch, Card } from 'antd'
import { PlusOutlined, DeleteOutlined, ExclamationCircleOutlined, SafetyOutlined } from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { sysRoleApi, type SysRoleVO } from '../../../api/sys'
import RoleForm from './RoleForm'

export default function RolePage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formAuthOpen, setFormAuthOpen] = useState(false)
  const [formData, setFormData] = useState<SysRoleVO | undefined>(undefined)
  const [authRoleId, setAuthRoleId] = useState<number>()

  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  const handleEdit = (record: SysRoleVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个角色吗？`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await sysRoleApi.deleteBatch(ids)
          message.success('删除成功')
          actionRef.current?.reload()
          setSelectedRowKeys([])
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  const handleStatusChange = async (id: number, status: number) => {
    try {
      await sysRoleApi.updateStatus(id, status)
      message.success(status === 1 ? '已启用' : '已禁用')
      actionRef.current?.reload()
    } catch {
      // error handled by interceptor
    }
  }

  const handleAuth = (record: SysRoleVO) => {
    setAuthRoleId(record.id)
    setFormAuthOpen(true)
  }

  const handleAuthSuccess = () => {
    setFormAuthOpen(false)
    setAuthRoleId(undefined)
    actionRef.current?.reload()
  }

  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    actionRef.current?.reload()
  }

  const dataScopeMap: Record<number, { text: string; color: string }> = {
    1: { text: '全部数据', color: 'blue' },
    2: { text: '本部门数据', color: 'green' },
    3: { text: '本部门及子部门', color: 'orange' },
    4: { text: '仅本人数据', color: 'purple' },
  }

  const columns: ProColumns<SysRoleVO>[] = [
    {
      title: '角色名称',
      dataIndex: 'roleName',
      width: 150,
    },
    {
      title: '角色编码',
      dataIndex: 'roleCode',
      width: 150,
      render: (_, record) => <Tag color="blue">{record.roleCode}</Tag>,
    },
    {
      title: '数据权限',
      dataIndex: 'dataScope',
      width: 150,
      valueType: 'select',
      valueEnum: dataScopeMap,
      render: (_, record) => {
        const info = dataScopeMap[record.dataScope!]
        return info ? <Tag color={info.color}>{info.text}</Tag> : '-'
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      valueType: 'select',
      valueEnum: {
        1: { text: '启用', status: 'Success' },
        0: { text: '禁用', status: 'Error' },
      },
      render: (_, record) => (
        <Switch
          checked={record.status === 1}
          size="small"
          onChange={(checked) => handleStatusChange(record.id, checked ? 1 : 0)}
        />
      ),
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleEdit(record)}>编辑</a>
          <a onClick={() => handleAuth(record)}>
            <SafetyOutlined /> 授权
          </a>
          <a onClick={() => handleDelete([record.id])} style={{ color: '#ff4d4f' }}>
            删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<SysRoleVO>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await sysRoleApi.page({
            pageNum: current,
            pageSize,
            keyword: rest.keyword,
            status: rest.status,
          })
          return {
            data: res.records,
            total: res.total,
            success: true,
          }
        }}
        toolbar={{
          actions: [
            <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增角色
            </Button>,
            <Button
              key="delete"
              danger
              icon={<DeleteOutlined />}
              disabled={selectedRowKeys.length === 0}
              onClick={() => handleDelete(selectedRowKeys as number[])}
            >
              批量删除
            </Button>,
          ],
        }}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
        }}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
          pageSizeOptions: ['10', '20', '50'],
        }}
        search={{
          labelWidth: 'auto',
          defaultCollapsed: true,
        }}
        dateFormatter="string"
      />

      <RoleForm
        open={formOpen}
        data={formData}
        onSuccess={handleFormSuccess}
        onCancel={() => {
          setFormOpen(false)
          setFormData(undefined)
        }}
      />
    </Card>
  )
}
